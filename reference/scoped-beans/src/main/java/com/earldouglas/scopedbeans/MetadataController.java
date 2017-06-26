package com.earldouglas.scopedbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MetadataController {

    @Autowired
    private Metadata metadata;

    @RequestMapping(value = "/metadata")
    public void metadata(Model model) {
        model.addAttribute("date", metadata.getDate());
    }
}
