package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.config.CacheConfig;
import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ConsultAreaService {

    private static final long ROOT = 0L;

    private final ConsultAreaRepository repository;

    public ConsultAreaService(ConsultAreaRepository repository) {
        this.repository = repository;
    }

    @Cacheable(CacheConfig.AREA_TREE)
    @Transactional(readOnly = true)
    public List<ConsultAreaNode> tree() {
        List<ConsultArea> all = repository.findAllByOrderBySortOrderAscIdAsc();
        Map<Long, List<ConsultArea>> byParent = new HashMap<>();
        for (ConsultArea area : all) {
            long parent = area.getParentId() == null ? ROOT : area.getParentId();
            byParent.computeIfAbsent(parent, k -> new ArrayList<>()).add(area);
        }
        return buildChildren(ROOT, byParent);
    }

    private List<ConsultAreaNode> buildChildren(Long parentId, Map<Long, List<ConsultArea>> byParent) {
        List<ConsultArea> children = byParent.getOrDefault(parentId, List.of());
        List<ConsultAreaNode> nodes = new ArrayList<>(children.size());
        for (ConsultArea area : children) {
            List<ConsultAreaNode> grandChildren = buildChildren(area.getId(), byParent);
            nodes.add(new ConsultAreaNode(area.getId(), area.getName(), area.getParentId(),
                    area.getSortOrder(), grandChildren));
        }
        return nodes;
    }

    @Transactional(readOnly = true)
    public ConsultArea getRequired(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "领域不存在"));
    }

    @Transactional(readOnly = true)
    public List<ConsultArea> findAll() {
        return repository.findAllByOrderBySortOrderAscIdAsc();
    }

    /**
     * 收集一批根领域及其所有子孙领域的 id（内存 BFS，避免 LIKE 前缀查询）。
     */
    @Transactional(readOnly = true)
    public Set<Long> subtreeIds(Collection<Long> rootIds) {
        if (rootIds == null || rootIds.isEmpty()) {
            return Set.of();
        }
        List<ConsultArea> all = repository.findAllByOrderBySortOrderAscIdAsc();
        Map<Long, List<Long>> childrenByParent = new HashMap<>();
        for (ConsultArea area : all) {
            if (area.getParentId() != null) {
                childrenByParent.computeIfAbsent(area.getParentId(), k -> new ArrayList<>()).add(area.getId());
            }
        }
        Set<Long> result = new LinkedHashSet<>();
        Deque<Long> queue = new ArrayDeque<>(rootIds);
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (result.add(current)) {
                queue.addAll(childrenByParent.getOrDefault(current, List.of()));
            }
        }
        return result;
    }

    @CacheEvict(value = CacheConfig.AREA_TREE, allEntries = true)
    @Transactional
    public ConsultArea create(ConsultAreaRequest request) {
        validateParent(request.parentId());
        ConsultArea area = new ConsultArea();
        area.setName(request.name());
        area.setParentId(request.parentId());
        area.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        return repository.save(area);
    }

    @CacheEvict(value = CacheConfig.AREA_TREE, allEntries = true)
    @Transactional
    public ConsultArea update(Long id, ConsultAreaRequest request) {
        ConsultArea area = getRequired(id);
        if (Objects.equals(request.parentId(), id)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "父领域不能是自身");
        }
        validateParent(request.parentId());
        area.setName(request.name());
        area.setParentId(request.parentId());
        if (request.sortOrder() != null) {
            area.setSortOrder(request.sortOrder());
        }
        return repository.save(area);
    }

    @CacheEvict(value = CacheConfig.AREA_TREE, allEntries = true)
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "领域不存在");
        }
        repository.deleteById(id);
    }

    private void validateParent(Long parentId) {
        if (parentId != null && !repository.existsById(parentId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "父领域不存在: " + parentId);
        }
    }
}
