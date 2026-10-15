package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.ProjectRequest;
import com.akenzhan.taskmanagement.entity.Project;
import com.akenzhan.taskmanagement.entity.User;
import com.akenzhan.taskmanagement.exception.ResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.ProjectMapper;
import com.akenzhan.taskmanagement.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMapper projectMapper;
    @Mock private UserService userService;

    @InjectMocks private ProjectService projectService;

    @Test
    void createProjectAssignsOwnerAndMarksActive() {
        User owner = User.builder().id(1L).username("alice").build();
        ProjectRequest request = new ProjectRequest();
        request.setName("Website redesign");
        request.setDescription("Q3 marketing site");
        when(userService.getUserEntityByUsername("alice")).thenReturn(owner);
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        projectService.createProject(request, "alice");

        ArgumentCaptor<Project> saved = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Website redesign");
        assertThat(saved.getValue().getOwner()).isSameAs(owner);
        assertThat(saved.getValue().isActive()).isTrue();
    }

    @Test
    void getProjectByIdThrowsWhenMissing() {
        when(projectRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void deleteProjectThrowsWhenMissing() {
        when(projectRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProject(7L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    void deleteProjectRemovesExistingProject() {
        when(projectRepository.existsById(7L)).thenReturn(true);

        projectService.deleteProject(7L);

        verify(projectRepository).deleteById(7L);
    }
}
