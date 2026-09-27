package cn.duckflew.education.resource;

import cn.duckflew.education.file.FileObjectRepository;
import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final StudyResourceRepository resourceRepository;
    private final FileObjectRepository fileObjectRepository;
    private final UserRepository userRepository;

    public ResourceService(StudyResourceRepository resourceRepository,
                           FileObjectRepository fileObjectRepository,
                           UserRepository userRepository) {
        this.resourceRepository = resourceRepository;
        this.fileObjectRepository = fileObjectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long publish(Long professorId, ResourceDtos.CreateRequest request) {
        if (!fileObjectRepository.existsById(request.fileId())) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        StudyResource resource = new StudyResource();
        resource.setProfessorId(professorId);
        resource.setName(request.name());
        resource.setFileId(request.fileId());
        resource.setRemark(request.remark());
        return resourceRepository.save(resource).getId();
    }

    @Transactional(readOnly = true)
    public Page<ResourceDtos.ResourceView> search(String keyword, Pageable pageable) {
        Page<StudyResource> page = (keyword == null || keyword.isBlank())
                ? resourceRepository.findAll(pageable)
                : resourceRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        return toViews(page);
    }

    @Transactional(readOnly = true)
    public Page<ResourceDtos.ResourceView> listMine(Long professorId, Pageable pageable) {
        return toViews(resourceRepository.findByProfessorId(professorId, pageable));
    }

    private Page<ResourceDtos.ResourceView> toViews(Page<StudyResource> page) {
        List<StudyResource> resources = page.getContent();
        if (resources.isEmpty()) {
            return new PageImpl<>(List.of(), page.getPageable(), page.getTotalElements());
        }
        Map<Long, String> names = userRepository.findAllById(
                        resources.stream().map(StudyResource::getProfessorId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, this::displayName));
        List<ResourceDtos.ResourceView> views = resources.stream().map(r -> new ResourceDtos.ResourceView(
                r.getId(), r.getName(), r.getFileId(), r.getRemark(), r.getProfessorId(),
                names.getOrDefault(r.getProfessorId(), "未知教授"), r.getCreatedAt())).toList();
        return new PageImpl<>(views, page.getPageable(), page.getTotalElements());
    }

    private String displayName(User user) {
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName();
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername() == null ? "用户" + user.getId() : user.getUsername();
    }
}
