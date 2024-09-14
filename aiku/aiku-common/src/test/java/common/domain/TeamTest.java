package common.domain;

import common.domain.member.Member;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TeamTest {

    @Test
    void create() {
        //given
        Member member = new Member("member1");

        //when
        String teamName = "team1";
        Team team = Team.create(member, teamName);

        //then
        assertThat(team.getTeamName()).isEqualTo(teamName);
        assertThat(team.getStatus()).isEqualTo(Status.ALIVE);
        assertThat(team.getTeamMembers().size()).isEqualTo(1);

        TeamMember teamMember = team.getTeamMembers().get(0);
        assertThat(teamMember.getTeam()).isEqualTo(team);
        assertThat(teamMember.getMember()).isEqualTo(member);
        assertThat(teamMember.isOwner()).isTrue();
        assertThat(teamMember.getStatus()).isEqualTo(Status.ALIVE);
    }

    @Test
    void addTeamMember() {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        String teamName = "team1";
        Team team = Team.create(member1, teamName);

        //when
        team.addTeamMember(member2, false);

        //then
        assertThat(team.getTeamName()).isEqualTo(teamName);
        assertThat(team.getStatus()).isEqualTo(Status.ALIVE);
        assertThat(team.getTeamMembers().size()).isEqualTo(2);

        List<TeamMember> teamMembers = team.getTeamMembers();
        assertThat(teamMembers).extracting("member").containsExactly(member1, member2);
        assertThat(teamMembers).extracting("team").containsExactly(team, team);
        assertThat(teamMembers).extracting("isOwner").containsExactly(true, false);
        assertThat(teamMembers).extracting("status").containsExactly(Status.ALIVE, Status.ALIVE);
    }
}