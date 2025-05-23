package com.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class RegistroUsuarioTest {

    private WebDriver driver;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @Test
    public void registrarNuevoUsuarioYRedirigirADashboard() throws InterruptedException {
        // Paso 1: Ingresar a la página principal
        driver.get("http://localhost:5000"); 

        // Paso 2: Hacer clic en el botón "Registrarse" desde home
        WebElement botonRegistroHome = driver.findElement(By.xpath("//a[text()='Registrarse']"));
        botonRegistroHome.click();

        // Paso 3: Rellenar el formulario de registro
        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));
        //WebElement confirmPassword = driver.findElement(By.xpath("/html/body/div/form/button"));

        String nombreAleatorio = "usuario" + System.currentTimeMillis();
        String passwString = "clave123";

        username.sendKeys(nombreAleatorio);
        password.sendKeys(passwString);
        // confirmPassword.sendKeys("clave123");

        // Paso 4: Enviar el formulario
        WebElement botonRegistro = driver.findElement(By.xpath("/html/body/div/form/button"));
        botonRegistro.click();

        // Paso 5: Esperar y verificar redirección a /login
        Thread.sleep(2500); 

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue("No se redirigió al login", currentUrl.contains("/login"));

        //Paso 6: Iniciar sesión con el nuevo usuario
        WebElement usernameLogin = driver.findElement(By.name("username"));
        WebElement passwordLogin = driver.findElement(By.name("password"));
        usernameLogin.sendKeys(nombreAleatorio);
        passwordLogin.sendKeys(passwString);

        // Paso 7: Hacer clic en el botón "Iniciar sesión"
        WebElement botonLogin = driver.findElement(By.xpath("/html/body/div/form/button"));
        botonLogin.click();

        // Paso 8: Esperar redirección a /dashboard 
        Thread.sleep(2500); 

        String currentUrlDashboard = driver.getCurrentUrl();
        Assert.assertTrue("No se redirigió al dashboard", currentUrlDashboard.contains("/dashboard"));

        // Paso 9: Verificar que el contenido de bienvenida esté presente
        WebElement saludo = driver.findElement(By.xpath("/html/body/div/div/h2"));
        Assert.assertTrue("No se encontró saludo", saludo.getText().toLowerCase().contains("bienvenido"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
