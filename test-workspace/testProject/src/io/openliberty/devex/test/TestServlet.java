package io.openliberty.devex.test;


import jakarta.servlet.annotation.WebServlet;

import org.junit.Test;

import componenttest.app.FATServlet;

@WebServlet("/*")
public class TestServlet extends FATServlet {


    @Test
    public void testSomeFeature() {

    }

}