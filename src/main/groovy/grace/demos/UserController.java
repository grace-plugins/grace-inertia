package grace.demos;

import org.graceframework.plugins.inertia.InertiaResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/index")
    public InertiaResponse index() {
        return InertiaResponse.component("user/index").build();
    }

    @GetMapping("/user/create")
    public InertiaResponse create() {
        return InertiaResponse.component("user/create").build();
    }

    @GetMapping("/user/show")
    public InertiaResponse show() {
        return InertiaResponse.component("user/show").build();
    }

    @GetMapping("/user/edit")
    public InertiaResponse edit() {
        return InertiaResponse.component("user/edit").build();
    }

}
