package com.ldiamond.archunittest.jparest.hasProblem;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProblemController {
	@GetMapping("/failmytest")
	public Jpatable failmytest(String something) {
		Jpatable owner = new Jpatable();
		return owner;
	}
}
