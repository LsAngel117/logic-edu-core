package com.logossystemsit.logiceducore.interfaces.rest.membership.controller;

import com.logossystemsit.logiceducore.application.membership.dto.command.*;
import com.logossystemsit.logiceducore.application.membership.dto.result.MembershipResult;
import com.logossystemsit.logiceducore.application.membership.port.in.*;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MembershipControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssignMembershipUseCase assignMembershipUseCase;

    @Mock
    private GetUserMembershipsUseCase getUserMembershipsUseCase;

    @Mock
    private ToggleMembershipUseCase toggleMembershipUseCase;

    @Mock
    private ChangeMembershipRoleUseCase changeMembershipRoleUseCase;

    @Mock
    private ChangeMembershipScopeUseCase changeMembershipScopeUseCase;

    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private MembershipController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void assignMembershipShouldReturn201WithResponse() throws Exception {
        AssignMembershipRequest request = new AssignMembershipRequest(
                "00000000-0000-0000-0000-000000000001",
                "STUDENT",
                "COURSE",
                "course-123"
        );
        UserId userId = new UserId(request.userId());
        Membership membership = Membership.create(userId, Role.STUDENT, Scope.course("course-123"));

        doNothing().when(assignMembershipUseCase).execute(any(AssignMembershipCommand.class));
        when(membershipRepository.findByUserId(any(UserId.class))).thenReturn(List.of(membership));

        mockMvc.perform(post("/api/v1/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.value()))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void getUserMembershipsShouldReturn200WithList() throws Exception {
        UserId userId = new UserId("00000000-0000-0000-0000-000000000001");
        MembershipResult m1 = new MembershipResult(
                "mem-1", userId.value(), "STUDENT", "COURSE", "course-123", true
        );
        MembershipResult m2 = new MembershipResult(
                "mem-2", userId.value(), "TEACHER", "COURSE", "course-456", true
        );

        when(getUserMembershipsUseCase.execute(any(UserId.class))).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/v1/memberships/users/{userId}", userId.value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].role").value("STUDENT"))
                .andExpect(jsonPath("$[1].role").value("TEACHER"));
    }

    @Test
    void deactivateMembershipShouldReturn204() throws Exception {
        MembershipId membershipId = MembershipId.generate();

        doNothing().when(toggleMembershipUseCase).deactivate(any(MembershipId.class));

        mockMvc.perform(delete("/api/v1/memberships/{id}", membershipId.value()))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateMembershipShouldReturn200WithResponse() throws Exception {
        MembershipId membershipId = MembershipId.generate();
        Membership membership = Membership.restore(
                membershipId,
                new UserId("00000000-0000-0000-0000-000000000001"),
                Role.STUDENT,
                Scope.course("course-123"),
                true
        );

        doNothing().when(toggleMembershipUseCase).activate(any(MembershipId.class));
        when(membershipRepository.findById(any(MembershipId.class))).thenReturn(Optional.of(membership));

        mockMvc.perform(put("/api/v1/memberships/{id}/activate", membershipId.value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void changeMembershipRoleShouldReturn200WithResponse() throws Exception {
        MembershipId membershipId = MembershipId.generate();
        ChangeMembershipRoleRequest request = new ChangeMembershipRoleRequest("TEACHER");
        Membership membership = Membership.restore(
                membershipId,
                new UserId("00000000-0000-0000-0000-000000000001"),
                Role.TEACHER,
                Scope.course("course-123"),
                true
        );

        doNothing().when(changeMembershipRoleUseCase).execute(any(ChangeMembershipRoleCommand.class));
        when(membershipRepository.findById(any(MembershipId.class))).thenReturn(Optional.of(membership));

        mockMvc.perform(patch("/api/v1/memberships/{id}/role", membershipId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TEACHER"));
    }

    @Test
    void changeMembershipScopeShouldReturn200WithResponse() throws Exception {
        MembershipId membershipId = MembershipId.generate();
        ChangeMembershipScopeRequest request = new ChangeMembershipScopeRequest("COURSE", "course-999");
        Membership membership = Membership.restore(
                membershipId,
                new UserId("00000000-0000-0000-0000-000000000001"),
                Role.STUDENT,
                Scope.course("course-999"),
                true
        );

        doNothing().when(changeMembershipScopeUseCase).execute(any(ChangeMembershipScopeCommand.class));
        when(membershipRepository.findById(any(MembershipId.class))).thenReturn(Optional.of(membership));

        mockMvc.perform(patch("/api/v1/memberships/{id}/scope", membershipId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scopeType").value("COURSE"))
                .andExpect(jsonPath("$.scopeRefId").value("course-999"));
    }
}
