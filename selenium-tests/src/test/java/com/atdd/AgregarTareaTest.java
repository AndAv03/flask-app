package com.atdd;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/****************************************/
// Historia de Usuario: Como usuario registrado quiero agregar una nueva tarea
// para mantener un seguimiento de mis pendientes.
//
// Prueba de Aceptación: Verificar que una tarea nueva aparece en la lista
// después de agregarla.
//  
// 1. Ingresar a la página principal del Gestor de Tareas
// 2. Hacer clic en el botón "Iniciar Sesión"
// 3. Rellenar el formulario de inicio de sesión con un usuario existente
// 4. Hacer clic en el botón "Ingresar"
// 5. Verificar que se redirige al dashboard
// 6. Ingresar el texto de la tarea en el campo correspondiente
// 7. Hacer clic en el botón "Agregar"
// 8. Verificar que la tarea aparece en la lista de tareas
// 
// 
// RESULTADO ESPERADO: La tarea agregada se muestra correctamente en la lista de tareas.
/****************************************/

public class AgregarTareaTest {

    private WebDriver driver;
    private String baseUrl;
    private String nombreUsuario;
    private String contraseña;

    @Before
    public void setUp() throws Exception {
        // Configuración de Chrome para ejecución en CI (headless y flags recomendados)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        baseUrl = System.getenv().getOrDefault("FLASK_BASE_URL", "http://localhost:5000");
        nombreUsuario = "testuser";
        contraseña = "123";
    }

    @Test
    public void agregarNuevaTareaYVerificarEnLista() throws InterruptedException {
        // Paso 1: Ingresar a la página principal
        driver.get(baseUrl);

        // Paso 2: Hacer clic en el botón "Iniciar Sesión" desde home
        WebElement botonLoginHome = driver.findElement(By.xpath("//a[text()='Iniciar Sesión']"));
        botonLoginHome.click();

        // Paso 3: Rellenar el formulario de inicio de sesión
        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));
        username.sendKeys(nombreUsuario);
        password.sendKeys(contraseña);

        // Paso 4: Hacer clic en el botón "Ingresar"
        WebElement botonLogin = driver.findElement(By.xpath("//button[text()='Ingresar']"));
        botonLogin.click();

        // Paso 5: Verificar que estamos en el dashboard
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue("No se redirigió al dashboard", currentUrl.contains("/dashboard"));

        // Paso 6: Ingresar el texto de la tarea en el campo correspondiente
        String textoTarea = "sasaassssssssssa122222222" + System.currentTimeMillis();
        WebElement campoTarea = driver.findElement(By.name("task"));
        campoTarea.clear();
        campoTarea.sendKeys(textoTarea);

        // Paso 7: Hacer clic en el botón "Agregar"
        WebElement botonAgregar = driver.findElement(By.xpath("//button[text()='Agregar']"));
        botonAgregar.click();

        // Esperar a que se actualice la lista
        Thread.sleep(2000);

        // Paso 8: Verificar que la tarea aparece en la lista
        List<WebElement> elementosTarea = driver.findElements(By.cssSelector(".list-group-item"));
        boolean tareaEncontrada = false;
        for (WebElement elemento : elementosTarea) {
            if (elemento.getText().contains(textoTarea)) {
                tareaEncontrada = true;
                break;
            }
        }
        Assert.assertTrue("La tarea agregada no se encontró en la lista", tareaEncontrada);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}