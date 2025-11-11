package com.example.goalie;

import com.example.goalie.team.TeamTemplate;
import com.example.goalie.team.TeamTemplate.PlayerInfo;
import com.example.goalie.team.TeamTemplate.MatchInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class TeamController {

    @GetMapping("/team")
    public String showTeam(Model model) {
        TeamTemplate team = new TeamTemplate();
        team.setTeamName("Douglas FC");
        team.setCoachName("Coach Nana");
        team.setTournamentName("Fall League 2025");

        team.setPlayers(List.of(
                new PlayerInfo("Samuel Kagimbi", "Striker", "Advanced", 10),
                new PlayerInfo("Uchechi Sebastian", "Goalkeeper", "Intermediate", 8),
                new PlayerInfo("Jude Enuanwa", "Midfielder", "Advanced", 9)
        ));

        team.setMatches(List.of(
                new MatchInfo("Langley Lions", "Completed", "2-1"),
                new MatchInfo("Burnaby Blazers", "Scheduled", "—")
        ));

        model.addAttribute("team", team);
        return "team";  // loads templates/team.html
    }
}
