package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.responses.UserDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.UserIndexResponse;
import eu.dickovadev.pojisteniapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/insured")
public class InsuredController {

    private static final Logger log = LoggerFactory.getLogger(InsuredController.class);

    private static final String VIEW_INDEX  = "pages/insured/index";
    private static final String VIEW_CREATE = "pages/insured/create";
    private static final String VIEW_EDIT = "pages/insured/edit";
    private static final String VIEW_CUSTOMER_EDIT = "pages/insured/customer-edit";
    private static final String VIEW_DETAIL = "pages/insured/detail";
    private static final String REDIRECT_ROOT = "redirect:/insured";
    public static final String REDIRECT_DETAIL = "redirect:/insured/%d/detail";

    private static final int INDEX_PAGE_SIZE  = 8;
    private static final int DETAIL_PAGE_SIZE = 4;

    private final UserService userService;

    @Autowired
    public InsuredController(UserService userService) {
        this.userService = userService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public String renderIndex(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "searchField", required = false, defaultValue = "userId") String searchField
    ) {

        String q = query == null ? null : query.trim().toLowerCase();
        log.info("GET /insured index page={} size={} searchField={} query='{}'", page, INDEX_PAGE_SIZE, searchField, q);

        UserIndexResponse response = userService.getPaginatedUsers(q, searchField, page, INDEX_PAGE_SIZE);

        model.addAttribute("insuredList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Zákazníci");

        return VIEW_INDEX;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/create")
    public String renderCreateForm(@ModelAttribute UserDTO user, Model model) {
        log.info("GET /insured/create");
        model.addAttribute("pageTitle", "Vytvořit profil");
        return VIEW_CREATE;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public String createInsured(
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes flash,
            HttpServletRequest request,
            Model model
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on create: {}", result.getErrorCount());
            return renderCreateForm(user, model); // stop on errors
        }

        long userId = userService.create(user);
        request.getSession().setAttribute("userId", userId);

        log.info("Created insured userId={}", userId);
        flash.addFlashAttribute("success", "Pojištěnec přidán.");
        return String.format(REDIRECT_DETAIL, userId);
    }

    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}/detail")
    public String renderDetail(
            @PathVariable long userId,
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        log.info("GET /insured/{}/detail page={} size={}", userId, page, DETAIL_PAGE_SIZE);

        UserDetailResponse response = userService.getUserWithPaginatedPolicies(userId, page, DETAIL_PAGE_SIZE);

        model.addAttribute("user", response.getUser());
        model.addAttribute("policies", response.getPaginatedPolicies());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Detail");

        return VIEW_DETAIL;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/edit")
    public String renderEditForm(
            @PathVariable Long userId,
            @ModelAttribute UserDTO user,
            Model model
    ) {
        log.info("GET /insured/{}/edit", userId);
        userService.getUserEditData(userId, user);
        model.addAttribute("pageTitle", "Upravit osobu");
        return VIEW_EDIT;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{userId}/edit")
    public String editInsured(
            @PathVariable long userId,
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes flash,
            Model model
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on admin edit userId={} errors={}", userId, result.getErrorCount());
            return renderEditForm(userId, user, model); // stop on errors
        }

        userService.editByAdmin(user, userId);
        log.info("Admin edited insured userId={}", userId);
        flash.addFlashAttribute("success", "Změny uloženy.");
        return String.format(REDIRECT_DETAIL, userId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{userId}/delete")
    public String deleteInsured(
            @PathVariable long userId,
            RedirectAttributes flash
    ) {
        userService.remove(userId);
        log.warn("Insured userId={} deleted by admin", userId);
        flash.addFlashAttribute("success", "Uživatel smazán.");
        return REDIRECT_ROOT;
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/{userId}/customer-edit")
    public String renderCustomerEditForm(
            @PathVariable Long userId,
            @ModelAttribute UserDTO user,
            Model model
    ) {
        log.info("GET /insured/{}/customer-edit", userId);
        userService.getUserEditData(userId, user);
        model.addAttribute("pageTitle", "Upravit osobu");
        return VIEW_CUSTOMER_EDIT;
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @PostMapping("/{userId}/customer-edit")
    public String editInsuredByCustomer(
            @PathVariable long userId,
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes flash,
            Model model
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on customer edit userId={} errors={}", userId, result.getErrorCount());
            return renderCustomerEditForm(userId, user, model); // stop on errors
        }

        userService.editByCustomer(user, userId);
        log.info("Customer edited self userId={}", userId);
        flash.addFlashAttribute("success", "Změny uloženy.");
        return String.format(REDIRECT_DETAIL, userId);
    }
}
