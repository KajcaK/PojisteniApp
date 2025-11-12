package eu.dickovadev.pojisteniapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String renderIndex(Model model) {
        model.addAttribute("pageTitle", "PojištěníApp");
        return "pages/home/index";
    }

    @GetMapping("/about")
    public String renderAbout(Model model) {
        model.addAttribute("pageTitle", "O PojištěníApp");
        return "pages/home/about";
    }

    @GetMapping("/policy-info")
    public String renderPolicyInfo(Model model) {
        model.addAttribute("pageTitle", "Pojištění");
        return "pages/home/policy-info";
    }

    @GetMapping("/event-info")
    public String renderEventInfo(Model model) {
        model.addAttribute("pageTitle", "Pojistné události");
        return "pages/home/event-info";
    }
}
