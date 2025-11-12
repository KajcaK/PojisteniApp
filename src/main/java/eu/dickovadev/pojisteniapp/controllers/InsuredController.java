package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.responses.UserDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.UserIndexResponse;
import eu.dickovadev.pojisteniapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    private final UserService userService;

    private static final String VIEW_INDEX  = "pages/insured/index";
    private static final String VIEW_CREATE = "pages/insured/create";
    private static final String VIEW_EDIT = "pages/insured/edit";
    private static final String VIEW_CUSTOMER_EDIT = "pages/insured/customer-edit";
    private static final String VIEW_DETAIL = "pages/insured/detail";
    private static final String REDIRECT_ROOT = "redirect:/insured";
    public static final String REDIRECT_DETAIL = "redirect:/insured/%d/detail";

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
        // Define the page size
        int pageSize = 8;

        if (query != null) query = query.trim().toLowerCase();

        UserIndexResponse response = userService.getPaginatedUsers(query, searchField, page, pageSize);

        // Add attributes to the model
        model.addAttribute("insuredList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Zákazníci");

        return VIEW_INDEX;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/create")
    public String renderCreateForm(
            @ModelAttribute UserDTO user,
            Model model
    ) {
        model.addAttribute("pageTitle", "Vytvořit profil");
        return VIEW_CREATE;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public String createInsured(
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            Model model
    ) {
        if (result.hasErrors())
            return renderCreateForm(user, model);

        long userId = userService.create(user);

        // Store userId in session after creation
        request.getSession().setAttribute("userId", userId);

        redirectAttributes.addFlashAttribute("success", "Pojištěnec přidán.");
        redirectAttributes.addAttribute("userId", userId);

        return String.format(REDIRECT_DETAIL, userId);
    }

    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}/detail")
    public String renderDetail(
            @PathVariable long userId,
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        // Define the page size
        int pageSize = 4;

        // Call the service method, passing the logged-in user's ID
        UserDetailResponse response = userService.getUserWithPaginatedPolicies(userId, page, pageSize);

        // Add attributes to the model
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
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors())
            return renderEditForm(userId, user, model);

        userService.editByAdmin(user, userId);

        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return String.format(REDIRECT_DETAIL, userId);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/delete")
    public String deleteInsured(
            @PathVariable long userId,
            RedirectAttributes redirectAttributes
    ) {
        userService.remove(userId);

        redirectAttributes.addFlashAttribute("success", "Uživatel smazán.");
        return REDIRECT_ROOT;
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/{userId}/customer-edit")
    public String renderCustomerEditForm(
            @PathVariable Long userId,
            @ModelAttribute UserDTO user,
            Model model
    ) {
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
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors())
            return renderCustomerEditForm(userId, user, model);

        userService.editByCustomer(user, userId);

        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return String.format(REDIRECT_DETAIL, userId);
    }
}
