package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.result.MembershipResult;
import com.logossystemsit.logiceducore.application.membership.port.in.GetUserMembershipsUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserMembershipsServiceTest {

    @Mock
    private MembershipRepository repository;

    private GetUserMembershipsUseCase useCase;

    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        useCase = new GetUserMembershipsService(repository);
    }

    @Test
    void execute_shouldReturnMembershipsForUser() {
        Membership m1 = Membership.restore(
                MembershipId.generate(), USER_ID, Role.STUDENT, Scope.course("course-1"), true);
        Membership m2 = Membership.restore(
                MembershipId.generate(), USER_ID, Role.TEACHER, Scope.course("course-2"), true);
        when(repository.findByUserId(USER_ID)).thenReturn(List.of(m1, m2));

        List<MembershipResult> results = useCase.execute(USER_ID);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).role()).isEqualTo("STUDENT");
        assertThat(results.get(1).role()).isEqualTo("TEACHER");
        verify(repository).findByUserId(USER_ID);
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoMemberships() {
        when(repository.findByUserId(USER_ID)).thenReturn(List.of());

        List<MembershipResult> results = useCase.execute(USER_ID);

        assertThat(results).isEmpty();
        verify(repository).findByUserId(USER_ID);
    }
}
