package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.models.responses.AdminUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.AuditLogResponse;
import eu.dickovadev.pojisteniapp.models.responses.NullUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.StatisticsResponse;
import eu.dickovadev.pojisteniapp.services.AdminService;
import eu.dickovadev.pojisteniapp.services.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private static final String VIEW_ADMIN_INDEX            = "pages/admin/index";
    private static final String FRAG_AUDIT_LOGS             = "fragments/admin-fragments :: auditLogsContent";
    private static final String FRAG_INCOMPLETE_USERS       = "fragments/admin-fragments :: nullUsersContent";
    private static final String FRAG_ADMIN_USERS            = "fragments/admin-fragments :: adminUsersContent";
    private static final String FRAG_STATISTICS             = "fragments/admin-fragments :: statisticsContent";

    private static final String REDIRECT_ADMIN_ROOT         = "redirect:/admin";
    private static final int    DEFAULT_PAGE_SIZE           = 10;

    private final AdminService adminService;
    private final StatisticsService statisticsService;

    @Autowired
    public AdminController(AdminService adminService, StatisticsService statisticsService) {
        this.adminService = adminService;
        this.statisticsService = statisticsService;
    }

    /* Small helper to redirect to /admin */
    private String safeRedirectBack(HttpServletRequest request) {
        String ref = request.getHeader("Referer");
        if (ref == null || !ref.contains("/admin")) return REDIRECT_ADMIN_ROOT;
        return "redirect:" + ref;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public String renderIndex(Model model) {
        log.info("Admin index requested");
        model.addAttribute("pageTitle", "Admin Sekce");
        model.addAttribute("useJQuery", true);
        return VIEW_ADMIN_INDEX;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/null-users")
    public String renderNullUsers(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        log.info("GET /admin/null-users page={} size={}", page, DEFAULT_PAGE_SIZE);
        NullUsersResponse resp = adminService.getPaginatedNullUsers(page, DEFAULT_PAGE_SIZE);

        model.addAttribute("nullUsersList", resp.getNullUsersList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resp.getPaginationMetadata().get("totalPages"));

        return FRAG_INCOMPLETE_USERS;
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-users")
    public String renderAdminUsers(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        log.info("GET /admin/admin-users page={} size={}", page, DEFAULT_PAGE_SIZE);
        AdminUsersResponse resp = adminService.getPaginatedAdminUsers(page, DEFAULT_PAGE_SIZE);

        model.addAttribute("adminList", resp.getAdminUsersList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resp.getPaginationMetadata().get("totalPages"));

        return FRAG_ADMIN_USERS;
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/audit-logs")
    public String renderAuditLogs(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        log.info("GET /admin/audit-logs page={} size={}", page, DEFAULT_PAGE_SIZE);
        AuditLogResponse resp = adminService.getAuditLogs(page, DEFAULT_PAGE_SIZE);

        model.addAttribute("logList", resp.getAuditLogDTOList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resp.getPaginationMetadata().get("totalPages"));

        log.debug("audit log count={}", resp.getAuditLogDTOList().size());
        return FRAG_AUDIT_LOGS;
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/statistics")
    public String renderStatistics(Model model) {
        log.info("GET /admin/statistics");
        StatisticsResponse stats = statisticsService.getStatistics();
        model.addAttribute("pageTitle", "Statistiky");
        model.addAttribute("statistics", stats);
        model.addAttribute("useCharts", true);
        return FRAG_STATISTICS;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/api/statistics")
    public ResponseEntity<StatisticsResponse> getStatisticsData() {
        log.info("GET /admin/api/statistics");
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{userId}/add-admin")
    public String addAdminRole(
            @PathVariable long userId,
            HttpServletRequest request,
            RedirectAttributes flash
    ) {
        log.info("POST grant ADMIN to userId={}", userId);
        try {
            adminService.setAdminRole(userId);
            flash.addFlashAttribute("success", "Uživateli " + userId + " přidána role správce.");
        } catch (UserNotFoundException ex) {
            log.warn("User not found for add-admin userId={}", userId, ex);
            flash.addFlashAttribute("error", "Uživatel " + userId + " nenalezen.");
        } catch (Exception ex) {
            log.error("Unexpected error while adding ADMIN to userId={}", userId, ex);
            flash.addFlashAttribute("error", "Nastala neočekávaná chyba při přidávání role.");
        }
        return safeRedirectBack(request);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{userId}/remove-admin")
    public String removeAdminRole(
            @PathVariable long userId,
            HttpServletRequest request,
            RedirectAttributes flash
    ) {
        log.info("DELETE revoke ADMIN from userId={}", userId);
        try {
            adminService.removeAdminRole(userId);
            flash.addFlashAttribute("success", "Uživateli " + userId + " odebrána role správce.");
        } catch (UserNotFoundException ex) {
            log.warn("User not found for remove-admin userId={}", userId, ex);
            flash.addFlashAttribute("error", "Uživatel " + userId + " nenalezen.");
        } catch (Exception ex) {
            log.error("Unexpected error while removing ADMIN from userId={}", userId, ex);
            flash.addFlashAttribute("error", "Nastala neočekávaná chyba při odebírání role.");
        }
        return safeRedirectBack(request);
    }
}
