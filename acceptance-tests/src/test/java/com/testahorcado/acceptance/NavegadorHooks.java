package com.testahorcado.acceptance;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class NavegadorHooks {

    static final String BASE_URL = System.getenv().getOrDefault("BASE_URL", "http://localhost:5173");
    private static final boolean HEADLESS = !"false".equalsIgnoreCase(System.getenv("HEADLESS"));
    private static final double SLOW_MO = Double.parseDouble(System.getenv().getOrDefault("SLOW_MO", "0"));

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    static Page page;

    @BeforeAll
    public static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(HEADLESS)
                .setSlowMo(SLOW_MO));
    }

    @AfterAll
    public static void cerrarNavegador() {
        browser.close();
        playwright.close();
    }

    @Before
    public void abrirPagina() {
        context = browser.newContext();
        page = context.newPage();
    }

    @After
    public void cerrarPagina(Scenario scenario) {
        if (scenario.isFailed()) {
            scenario.attach(page.screenshot(), "image/png", "captura");
        }

        context.close();
    }

}
