package com.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.URL;


import java.util.concurrent.TimeUnit;

/****************************************/
// Historia de Usuario: Como nuevo usuario quiero registrarme
// para acceder al panel de tareas.
//
// Prueba de Aceptación: Verificar que el registro exitoso
// redirige al dashboard con un mensaje de bienvenida.
//
// 1. Ingresar a la página principal del Gestor de Tareas
// 2. Hacer clic en el botón de "Registrarse"
// 3. Llenar los campos del formulario de registro: usuario y contraseña
// 4. Presionar el botón "Registrar"
// 5. Verificar redirección a la página de login
// 6. Llenar el formulario de inicio de sesión con el nuevo usuario registrado
// 7. Hacer clic en el botón "Iniciar sesión"
// 8. Verificar redirección a la página de dashboard

// RESULTADO ESPERADO: Se debe observar un mensaje de bienvenida, con el nombre del usuario registrado en los pasos 3 y 4.
/****************************************/

public class RegistroUsuarioTest {

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        baseUrl = System.getenv().getOrDefault("FLASK_BASE_URL", "http://localhost:5000");
    }

    @Test
    public void registrarNuevoUsuarioYRedirigirADashboard() throws InterruptedException {
        // Paso 1: Ingresar a la página principal
        driver.get(baseUrl);

        // Paso 2: Hacer clic en el botón "Registrarse" desde home
        WebElement botonRegistroHome = driver.findElement(By.xpath("//a[text()='Registrarse']"));
        botonRegistroHome.click();

        // Paso 3: Rellenar el formulario de registro
        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));
        String nombreAleatorio = "usuario" + System.currentTimeMillis();
        String passwString = "clave123";
        username.sendKeys(nombreAleatorio);
        password.sendKeys(passwString);

        // Paso 4: Presionar el botón "Registrar"
        WebElement botonRegistro = driver.findElement(By.xpath("//button[text()='Registrarse']"));
        botonRegistro.click();

        // Paso 5: Esperar y verificar redirección a /login
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue("No se redirigió al login", currentUrl.contains("/login"));

        // Paso 6: Llenar el formulario de inicio de sesión con el nuevo usuario registrado
        WebElement usernameLogin = driver.findElement(By.name("username"));
        WebElement passwordLogin = driver.findElement(By.name("password"));
        usernameLogin.sendKeys(nombreAleatorio);
        passwordLogin.sendKeys(passwString);

        // Paso 7: Hacer clic en el botón "Iniciar sesión"
        WebElement botonLogin = driver.findElement(By.xpath("//button[text()='Ingresar']"));
        botonLogin.click();

        // Paso 8: Esperar redirección a /dashboard
        Thread.sleep(2000);
        String currentUrlDashboard = driver.getCurrentUrl();
        Assert.assertTrue("No se redirigió al dashboard", currentUrlDashboard.contains("/dashboard"));

        // Verificación del resultado esperado
        WebElement saludo = driver.findElement(By.xpath("//h2[contains(text(),'Bienvenido')]"));
        Assert.assertTrue("No se encontró saludo", saludo.getText().toLowerCase().contains(nombreAleatorio.toLowerCase()));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
