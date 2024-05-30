package com.ldiamond.archunittest.jparest.hasNoProblem;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoodController {
	@GetMapping("/passmytest")
	public DataTransferObject passmytest(String something) {
		DataTransferObject owner = new DataTransferObject();
		return owner;
	}
}
