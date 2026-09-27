package cn.duckflew.education.guide;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.taxonomy.ConsultAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GuideService {

    private static final long ROOT = 0L;

    private final StudyGuideRepository guideRepository;
    private final GuideAreaRepository guideAreaRepository;
    private final ConsultAreaService consultAreaService;

    public GuideService(StudyGuideRepository guideRepository, GuideAreaRepository guideAreaRepository,
                        ConsultAreaService consultAreaService) {
        this.guideRepository = guideRepository;
        this.guideAreaRepository = guideAreaRepository;
        this.consultAreaService = consultAreaService;
    }

    @Transactional(readOnly = true)
    public List<GuideDtos.GuideNode> tree() {
        return buildTree(guideRepository.findAllByOrderBySortOrderAscIdAsc());
    }

    @Transactional(readOnly = true)
    public Optional<GuideDtos.GuideNode> findNode(Long id) {
        return flatten(tree()).stream().filter(n -> n.id().equals(id)).findFirst();
    }

    @Transactional(readOnly = true)
    public List<GuideDtos.GuideNode> recommendByAreas(Collection<Long> areaIds) {        Set<Long> subtree = consultAreaService.subtreeIds(areaIds);
        if (subtree.isEmpty()) {
            return List.of();
        }
        List<Long> matched = guideAreaRepository.findByAreaIdIn(new ArrayList<>(subtree)).stream()
                .map(GuideArea::getGuideId).distinct().toList();
        if (matched.isEmpty()) {
            return List.of();
        }
        List<StudyGuide> all = guideRepository.findAllByOrderBySortOrderAscIdAsc();
        Map<Long, StudyGuide> byId = new HashMap<>();
        all.forEach(g -> byId.put(g.getId(), g));

        Set<Long> keep = new HashSet<>();
        for (Long guideId : matched) {
            StudyGuide current = byId.get(guideId);
            while (current != null && keep.add(current.getId())) {
                current = current.getParentId() == null ? null : byId.get(current.getParentId());
            }
        }
        return buildTree(all.stream().filter(g -> keep.contains(g.getId())).toList());
    }

    @Transactional(readOnly = true)
    public List<GuideDtos.GuideNode> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return guideRepository.findByNameContainingIgnoreCaseOrderByIdAsc(keyword.trim()).stream()
                .map(g -> new GuideDtos.GuideNode(g.getId(), g.getName(), g.getParentId(),
                        g.isImportant(), g.getSortOrder(), List.of()))
                .toList();
    }

    @Transactional
    public StudyGuide create(GuideDtos.SaveRequest request) {
        validateParent(request.parentId());
        StudyGuide guide = new StudyGuide();
        apply(guide, request);
        return guideRepository.save(guide);
    }

    @Transactional
    public StudyGuide update(Long id, GuideDtos.SaveRequest request) {
        StudyGuide guide = getRequired(id);
        if (Objects.equals(request.parentId(), id)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "父节点不能是自身");
        }
        validateParent(request.parentId());
        apply(guide, request);
        return guideRepository.save(guide);
    }

    @Transactional
    public void delete(Long id) {
        if (!guideRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "节点不存在");
        }
        guideRepository.deleteById(id);
    }

    @Transactional
    public void setAreas(Long guideId, List<Long> areaIds) {
        getRequired(guideId);
        guideAreaRepository.deleteByGuideId(guideId);
        List<GuideArea> entities = areaIds.stream().distinct().map(areaId -> {
            GuideArea entity = new GuideArea();
            entity.setGuideId(guideId);
            entity.setAreaId(areaId);
            return entity;
        }).toList();
        guideAreaRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public List<Long> areasOf(Long guideId) {
        return guideAreaRepository.findByGuideId(guideId).stream().map(GuideArea::getAreaId).toList();
    }

    private List<GuideDtos.GuideNode> buildTree(List<StudyGuide> guides) {
        Map<Long, List<StudyGuide>> byParent = new HashMap<>();
        for (StudyGuide guide : guides) {
            long parent = guide.getParentId() == null ? ROOT : guide.getParentId();
            byParent.computeIfAbsent(parent, k -> new ArrayList<>()).add(guide);
        }
        return buildChildren(ROOT, byParent);
    }

    private List<GuideDtos.GuideNode> buildChildren(Long parentId, Map<Long, List<StudyGuide>> byParent) {
        List<StudyGuide> children = byParent.getOrDefault(parentId, List.of());
        List<GuideDtos.GuideNode> nodes = new ArrayList<>(children.size());
        for (StudyGuide guide : children) {
            nodes.add(new GuideDtos.GuideNode(guide.getId(), guide.getName(), guide.getParentId(),
                    guide.isImportant(), guide.getSortOrder(), buildChildren(guide.getId(), byParent)));
        }
        return nodes;
    }

    private List<GuideDtos.GuideNode> flatten(List<GuideDtos.GuideNode> nodes) {
        List<GuideDtos.GuideNode> result = new ArrayList<>();
        Deque<GuideDtos.GuideNode> stack = new ArrayDeque<>(nodes);
        while (!stack.isEmpty()) {
            GuideDtos.GuideNode node = stack.pop();
            result.add(node);
            stack.addAll(node.children());
        }
        return result;
    }

    private void apply(StudyGuide guide, GuideDtos.SaveRequest request) {
        guide.setName(request.name());
        guide.setParentId(request.parentId());
        guide.setImportant(request.important() != null && request.important());
        if (request.sortOrder() != null) {
            guide.setSortOrder(request.sortOrder());
        }
    }

    private void validateParent(Long parentId) {
        if (parentId != null && !guideRepository.existsById(parentId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "父节点不存在: " + parentId);
        }
    }

    private StudyGuide getRequired(Long id) {
        return guideRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "节点不存在"));
    }
}
