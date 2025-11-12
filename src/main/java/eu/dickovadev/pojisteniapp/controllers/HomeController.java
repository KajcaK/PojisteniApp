package eu.dickovadev.pojisteniapp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private static final String VIEW_HOME_INDEX     = "pages/home/index";
    private static final String VIEW_HOME_ABOUT     = "pages/home/about";
    private static final String VIEW_POLICY_INFO    = "pages/home/policy-info";
    private static final String VIEW_EVENT_INFO     = "pages/home/event-info";

    @GetMapping("/")
    public String renderIndex(Model model) {
        log.info("GET / -> home index");
        model.addAttribute("pageTitle", "PojištěníApp");
        return VIEW_HOME_INDEX;
    }

    @GetMapping("/about")
    public String renderAbout(Model model) {
        log.info("GET /about");
        model.addAttribute("pageTitle", "O PojištěníApp");
        return VIEW_HOME_ABOUT;
    }

    @GetMapping("/policy-info")
    public String renderPolicyInfo(Model model) {
        log.info("GET /policy-info");
        model.addAttribute("pageTitle", "Pojištění");
        return VIEW_POLICY_INFO;
    }

    @GetMapping("/event-info")
    public String renderEventInfo(Model model) {
        log.info("GET /event-info");
        model.addAttribute("pageTitle", "Pojistné události");
        return VIEW_EVENT_INFO;
    }
}
