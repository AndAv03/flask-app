package com.atdd;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.URL;
import java.util.List;

/****************************************/
// Historia de Usuario: Como usuario quiero eliminar una tarea
// para mantener mi lista de pendientes actualizada.
//
// Prueba de Aceptación: Verificar que una tarea desaparece de la lista
// después de eliminarla.
//
// 1. Iniciar sesión (registrar usuario si es necesario)
// 2. Asegurarse de que hay al menos una tarea visible (si no, agregar una)
// 3. Hacer clic en el botón de "Eliminar" junto a la tarea
// 4. Esperar que se actualice la lista
// 5. Verificar que la tarea ya no está visible en la lista
//
// RESULTADO ESPERADO: La tarea eliminada ya no debe aparecer en la lista de tareas.
/****************************************/

public class EliminarTareaTest {

    private WebDriver driver;
    private String baseUrl;

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
        driver.manage().window().maximize();
        baseUrl = System.getenv().getOrDefault("FLASK_BASE_URL", "http://localhost:5000");
    }

    @Test
    public void eliminarTareaExistenteYConfirmarDesaparicion() throws InterruptedException {
        // Paso 1: Iniciar sesión (registrar usuario si es necesario)
        driver.get(baseUrl + "/register");
        String usuario = "usuario" + System.currentTimeMillis();
        String clave = "clave123";
        driver.findElement(By.name("username")).sendKeys(usuario);
        driver.findElement(By.name("password")).sendKeys(clave);
        driver.findElement(By.xpath("//button[text()='Registrarse']")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("username")).sendKeys(usuario);
        driver.findElement(By.name("password")).sendKeys(clave);
        driver.findElement(By.xpath("//button[text()='Ingresar']")).click();
        Thread.sleep(1000);

        // Paso 2: Asegurarse de que hay al menos una tarea visible (si no, agregar una)
        String tituloTarea = "Tarea para eliminar " + System.currentTimeMillis();
        WebElement inputTarea = driver.findElement(By.name("task"));
        Assert.assertTrue("Task input field not found", inputTarea.isDisplayed());
        inputTarea.sendKeys(tituloTarea);
        driver.findElement(By.xpath("//button[text()='Agregar']")).click();

        // Esperar explícitamente a que la lista de tareas sea visible en el DOM
        WebDriverWait wait = new WebDriverWait(driver, 30); // Increase timeout to 30 seconds
        WebElement ul = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.list-group")));
        Assert.assertNotNull("Task list element missing", ul);

        // Buscar el <li> de la tarea agregada
        WebElement liTarea = null;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 20000) {
            List<WebElement> items = ul.findElements(By.tagName("li"));
            for (WebElement item : items) {
                if (item.getText().contains(tituloTarea)) {
                    liTarea = item;
                    break;
                }
            }
            if (liTarea != null) break;
            Thread.sleep(500);
        }
        if (liTarea == null) {
            throw new AssertionError("Task not found after adding. Current DOM: " + driver.getPageSource());
        }

        // Paso 3: Hacer clic en el botón de "Eliminar" junto a la tarea
        WebElement botonEliminar = null;
        try {
            botonEliminar = liTarea.findElement(By.xpath(".//a[contains(@class,'btn-outline-danger')]"));
        } catch (NoSuchElementException e) {
            throw new AssertionError("No se encontró el botón Eliminar para la tarea. HTML actual: " + liTarea.getAttribute("outerHTML"));
        }
        botonEliminar.click();

        // Paso 4: Esperar que se actualice la lista
        Thread.sleep(1000);

        // Paso 5: Verificar que la tarea ya no está visible en la lista
        boolean tareaPresente = false;
        List<WebElement> itemsDespues = ul.findElements(By.tagName("li"));
        for (WebElement item : itemsDespues) {
            if (item.getText().contains(tituloTarea)) {
                tareaPresente = true;
                break;
            }
        }
        Assert.assertFalse("La tarea eliminada aún está presente en la lista", tareaPresente);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}