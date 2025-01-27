package eu.dickovadev.pojisteniapp.controllers;


import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.EmailAlreadyRegisteredException;
import eu.dickovadev.pojisteniapp.models.mappers.UserMapper;
import eu.dickovadev.pojisteniapp.models.services.PaginationService;
import eu.dickovadev.pojisteniapp.models.services.RoleFilterService;
import eu.dickovadev.pojisteniapp.models.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/insured")
public class InsuredController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private RoleFilterService roleFilterService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public String renderIndex(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ){
        // Define the page size
        int pageSize = 8;

        // Get all user profiles
        List<UserProfileDTO> insuredList = userService.getAll();

        // Filter the list based on roles (admins will not be listed)
        List<UserProfileDTO> filteredList = roleFilterService.filterUsersExcludingAdmins(insuredList);

        // Paginate the filtered list
        List<UserProfileDTO> paginatedList = paginationService.paginate(filteredList, page, pageSize);

        // Get calculated metadata
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(filteredList, page, pageSize);

        // Add attributes to the model
        model.addAttribute("insuredList", paginatedList);
        model.addAttribute("currentPage", paginationMetadata.get("currentPage"));
        model.addAttribute("totalPages", paginationMetadata.get("totalPages"));
        model.addAttribute("totalItems", paginationMetadata.get("totalItems"));

        return "pages/insured/index";
    }

    @GetMapping("/create")
    public String renderCreateForm(@ModelAttribute UserProfileDTO user){
        return "pages/insured/create";
    }

    @PostMapping("/create")
    public String createInsured(
            @Valid @ModelAttribute UserProfileDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors())
            return renderCreateForm(user);

        userService.create(user);
        redirectAttributes.addFlashAttribute("success", "Pojištěnec přidán.");

        return "redirect:/insured";
    }

    @GetMapping("/detail/{userId}")
    public String renderDetail(
            @PathVariable long userId,
            Model model
    ){
        UserProfileDTO user = userService.getById(userId);
        model.addAttribute("user", user);

        return "pages/insured/detail";
    }

    @GetMapping("edit/{userId}")
    public String renderEditForm(
            @PathVariable Long userId,
            UserProfileDTO user
    ){
        UserProfileDTO fetchedUser = userService.getById(userId);
        userMapper.updateUserProfileDTO(fetchedUser, user);

        return "pages/insured/edit";
    }

    @PostMapping("edit/{userId}")
    public String editInsured(
            @PathVariable long userId,
            @Valid UserProfileDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors())
            return renderEditForm(userId, user);

        user.setUserId(userId);
        userService.edit(user);
        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return "redirect:/insured";
    }

    @GetMapping("delete/{userId}")
    public String deleteInsured(
            @PathVariable long userId,
            RedirectAttributes redirectAttributes
    ){
        userService.remove(userId);
        redirectAttributes.addFlashAttribute("success", "Uživatel smazán.");

        return "redirect:/insured";
    }

    @ExceptionHandler({DuplicateEmailException.class})
    public String handleDuplicateEmailException(
            RedirectAttributes redirectAttributes
    ){
        redirectAttributes.addFlashAttribute("error", "Tento email je již registrován. Zkuste jiný.");
        return "redirect:/insured";
    }

    @ExceptionHandler({EmailAlreadyRegisteredException.class})
    public String handleEmailAlreadyRegisteredException(
            RedirectAttributes redirectAttributes
    ){
        redirectAttributes.addFlashAttribute("error", "Tento email již patří jinému uživateli.");
        return "redirect:/insured";
    }
}
