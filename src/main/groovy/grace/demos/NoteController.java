package grace.demos;

import org.graceframework.plugins.inertia.Inertia;
import org.graceframework.plugins.inertia.InertiaPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NoteController {

    @GetMapping("/note/index")
    public InertiaPage index() {
        return InertiaPage.of("note/index");
    }

    @GetMapping("/note/create")
    public ModelAndView create() {
        return Inertia.render("note/create");
    }

    @GetMapping("/note/show")
    public InertiaPage show() {
        return InertiaPage.of("note/show");
    }

    @GetMapping("/note/edit")
    public InertiaPage edit() {
        return InertiaPage.of("note/edit");
    }

}
