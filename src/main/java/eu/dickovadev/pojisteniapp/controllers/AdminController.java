package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.responses.AdminUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.AuditLogResponse;
import eu.dickovadev.pojisteniapp.models.responses.NullUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.StatisticsResponse;
import eu.dickovadev.pojisteniapp.services.AdminService;
import eu.dickovadev.pojisteniapp.services.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final StatisticsService statisticsService;

    @Autowired
    public AdminController(AdminService adminService, StatisticsService statisticsService) {
        this.adminService = adminService;
        this.statisticsService = statisticsService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public String renderIndex(Model model) {

        model.addAttribute("pageTitle", "Admin Sekce");
        model.addAttribute("useJQuery", true);

        return "pages/admin/index";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/null-users")
    public String renderNullUsers(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        int pageSize = 10;

        NullUsersResponse response = adminService.getPaginatedNullUsers(page, pageSize);

        model.addAttribute("nullUsersList", response.getNullUsersList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));

        return "fragments/admin-fragments.html :: nullUsersContent";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-users")
    public String renderAdminUsers(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {

        int pageSize = 10;

        AdminUsersResponse response = adminService.getPaginatedAdminUsers(page, pageSize);

        model.addAttribute("adminList", response.getAdminUsersList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));

        return "fragments/admin-fragments.html :: adminUsersContent";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/audit-logs")
    public String renderAuditLogs(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {

        int pageSize = 10;

        AuditLogResponse response = adminService.getAuditLogs(page, pageSize);

        model.addAttribute("logList", response.getAuditLogDTOList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));

        return "fragments/admin-fragments.html :: auditLogsContent";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/statistics")
    public String renderStatistics(
            Model model
    ) {
        StatisticsResponse statisticsResponse = statisticsService.getStatistics();

        model.addAttribute("pageTitle", "Statistiky");
        model.addAttribute("statistics", statisticsResponse);
        model.addAttribute("useCharts", true);

        return "pages/admin/statistics";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/api/statistics")
    public ResponseEntity<StatisticsResponse> getStatisticsData() {
        StatisticsResponse statisticsResponse = statisticsService.getStatistics();
        return ResponseEntity.ok(statisticsResponse);  // This will return JSON response
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/add-admin")
    public String addAdminRole(
            @PathVariable long userId,
            Model model,
            HttpServletRequest request
    ) {
        String referer = request.getHeader("Referer");

        try {
            adminService.setAdminRole(userId);
            model.addAttribute("success", "Uživateli " + userId + " přidána role správce.");
        } catch (Exception e) {
            model.addAttribute("error", "Chyba při přidávání role uživateli " + userId);
        }

        if (referer != null) {
            return "redirect:" + referer;
        }
        return "redirect:/admin";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/remove-admin")
    public String removeAdminRole(
            @PathVariable long userId,
            Model model,
            HttpServletRequest request
    ) {
        String referer = request.getHeader("Referer");

        try {
            adminService.removeAdminRole(userId);
            model.addAttribute("success", "Uživateli " + userId + " odebrána role správce.");
        } catch (Exception e) {
            model.addAttribute("error", "Chyba při odebrání role uživateli " + userId);
        }

        if (referer != null) {
            return "redirect:" + referer;
        }
        return "redirect:/admin";
    }
}
