package com.cheil.web.controllers;

import com.cheil.core.mongo.dto.LibraryDto;
import com.cheil.core.mongo.model.Action;
import com.cheil.core.mongo.model.Library;
import com.cheil.core.mongo.model.Media;
import com.cheil.core.mongo.repository.ActionRepository;
import com.cheil.core.mongo.repository.LibraryRepository;
import com.cheil.core.mongo.repository.MediaRepository;
import com.cheil.core.mongo.repository.SubsidiaryRepository;
import com.cheil.core.service.SubsidiaryService;
import com.cheil.form.PromotionForm;
import com.cheil.form.ToolkitForm;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/finder")
public class FinderController extends BaseController {

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private Environment env;

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @GetMapping("/find")
    @ResponseBody
    public ModelAndView getImportForm(Model model) {
        model.addAttribute("editForm", new ToolkitForm());
        model.addAttribute("subsidiaries", subsidiaryService.findByStatusAndUserOrderByIdentifier(true));
        String[] types = env.getProperty("library.types").split(",");
        model.addAttribute("errorTypes", types);
        return new ModelAndView("finder/showlibrary");
    }

    @GetMapping("/findpromotion")
    @ResponseBody
    public ModelAndView findPromotion(Model model) {
        model.addAttribute("editForm", new PromotionForm());
        model.addAttribute("subsidiaries", subsidiaryService.findByStatusAndUserOrderByIdentifier(true));
        return new ModelAndView("finder/showpromotion");
    }

    @PostMapping("/find")
    @ResponseBody
    public ModelAndView generate(HttpSession session, @ModelAttribute("editForm") LibraryDto libraryForm, Model model) throws ParseException {
        model.addAttribute("subsidiaries", subsidiaryService.findByStatusAndUserOrderByIdentifier(true));
        String[] types = env.getProperty("library.types").split(",");
        model.addAttribute("errorTypes", types);
        session.setAttribute("libraryForm", libraryForm);

        Iterable<Library> libraries = libraryRepository.findAll();
        List<Library> result = new ArrayList<>();
        List<Library> finalFiltered = new ArrayList<>();
        libraries.forEach(result::add);
        List<Library> filtered = result.stream().filter(library -> library.getType().contains(libraryForm.getType())).collect(Collectors.toList());

        if (null != libraryForm.getSubsidiary()) {
            filtered = filtered.stream().filter(library -> library.getSubsidiaries().contains(subsidiaryRepository.findByRecordId(libraryForm.getSubsidiary()).getIdentifier())).collect(Collectors.toList());
        }
        boolean matched = false;
        if (StringUtils.isNotEmpty(libraryForm.getErrorMsg())) {
            for (Library library : filtered) {
                if (library.getPicEmail() != null && library.getPicEmail().contains(libraryForm.getErrorMsg())) {
                    matched = true;
                    finalFiltered.add(library);
                }
                if (library.getPicEmail() != null && library.getPicEmail().contains(libraryForm.getErrorMsg())) {
                    matched = true;
                    finalFiltered.add(library);
                }
                if (library.getShortDescription() != null && library.getShortDescription().contains(libraryForm.getErrorMsg())) {
                    matched = true;
                    finalFiltered.add(library);
                }
                if (library.getSubLibraries() != null) {
                    for (String subLib : library.getSubLibraries()) {
                        Library lib = libraryRepository.findByRecordId(subLib);
                        if (lib != null) {
                            if (lib.getId().toString().contains(libraryForm.getErrorMsg())) {
                                matched = true;
                                finalFiltered.add(library);
                            }
                            if (lib.getShortDescription().contains(libraryForm.getErrorMsg())) {
                                matched = true;
                                finalFiltered.add(library);
                            }
                        }
                    }
                }
                if (library.getActions() != null) {
                    for (String action : library.getActions()) {
                        Action action1 = actionRepository.findByRecordId(action);
                        if (action1 != null) {
                            if (action1.getLongDescription() != null && action1.getLongDescription().contains(libraryForm.getErrorMsg())) {
                                finalFiltered.add(library);
                                matched = true;
                            }
                            if (action1.getId() != null && action1.getId().toString().contains(libraryForm.getErrorMsg())) {
                                finalFiltered.add(library);
                                matched = true;
                            }
                            if (action1.getSystemPath() != null && action1.getSystemPath().contains(libraryForm.getErrorMsg())) {
                                finalFiltered.add(library);
                                matched = true;
                            }
                            if (action1.getShortDescription() != null && action1.getShortDescription().contains(libraryForm.getErrorMsg())) {
                                finalFiltered.add(library);
                                matched = true;
                            }
                        }
                    }
                }
                if (library.getMedias() != null) {
                    for (String media : library.getMedias()) {
                        //TODO Check if this is correctly fetching the data
                        Media media1 = mediaRepository.findByRecordId(media);
                        if (media1 != null) {
                            if (media1.getShortDescription() != null && media1.getShortDescription().contains(libraryForm.getErrorMsg())) {
                                finalFiltered.add(library);
                                matched = true;
                            }
                        }
                    }
                }
            }
        }
        if (!matched) {
            finalFiltered = filtered;
        }

        model.addAttribute("libraries", finalFiltered);
        return new ModelAndView("finder/find");
    }

    @PostMapping("/findpromotion")
    @ResponseBody
    public ModelAndView findpromotion(@ModelAttribute("editForm") PromotionForm promotionForm, Model model) throws ParseException {
        model.addAttribute("subsidiaries", subsidiaryService.findByStatusAndUserOrderByIdentifier(true));
        List<PromotionForm> forms = new ArrayList<>();
        forms.add(promotionForm);
        model.addAttribute("promotions", forms);
        return new ModelAndView("finder/findPromotion");
    }
}
