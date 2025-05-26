package com.atdd;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/****************************************/
// Historia de Usuario: Como usuario quiero eliminar una tarea
// para mantener mi lista de pendientes actualizada.
//
// Prueba de Aceptación: Verificar que una tarea desaparece de la lista
// después de eliminarla.
//
// 1. Acceder a la página principal del Gestor de Tareas
// Paso 2: Hacer clic en el botón "Registrarse" desde home
// Paso 3: registrar usuario 
// Paso 4: Asegurarse de que hay al menos una tarea visible (si no, agregar una)
// Paso 5: Hacer clic en el botón de "Eliminar" junto a la tarea
// Paso 6: Esperar que la tarea desaparezca de la lista
// Paso 7: Verificar que la tarea ya no está visible en la lista
// RESULTADO ESPERADO: La tarea eliminada ya no debe aparecer en la lista de tareas.
/****************************************/

public class EliminarTareaTest {

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @Test
    public void eliminarTareaExistenteYConfirmarDesaparicion() throws InterruptedException {
        // Paso 1 : Acceder a la página principal del Gestor de Tareas
        baseUrl = "http://localhost:5000"; // URL base del Gestor de Tareas
        driver.get(baseUrl);

         // Paso 2: Hacer clic en el botón "Registrarse" desde home
        WebElement botonRegistroHome = driver.findElement(By.xpath("//a[text()='Registrarse']"));
        botonRegistroHome.click();


        // Paso 3: registrar usuario si es necesario)
        Thread.sleep(2000);
        String usuario = "usuario" + System.currentTimeMillis();
        String clave = "clave123";
        driver.findElement(By.name("username")).sendKeys(usuario);
        driver.findElement(By.name("password")).sendKeys(clave);
        driver.findElement(By.xpath("//button[text()='Registrarse']")).click();

        WebDriverWait wait = new WebDriverWait(driver, 120);

        // Esperar a que el formulario de login esté visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        driver.findElement(By.name("username")).sendKeys(usuario);
        driver.findElement(By.name("password")).sendKeys(clave);
        driver.findElement(By.xpath("//button[text()='Ingresar']")).click();

        // Esperar a que el campo de tarea esté visible
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("task")));
        Thread.sleep(1000);
        // Paso 4: Asegurarse de que hay al menos una tarea visible (si no, agregar una)
        String tituloTarea = "Tarea";
        WebElement inputTarea = driver.findElement(By.name("task"));
        Assert.assertTrue("Task input field not found", inputTarea.isDisplayed());
        inputTarea.sendKeys(tituloTarea);
        driver.findElement(By.xpath("//button[text()='Agregar']")).click();

        // Esperar robustamente a que la lista de tareas esté presente
        Thread.sleep(1000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.list-group")));

        // Esperar a que el <li> de la tarea agregada esté presente usando stream
        WebElement liTarea;
        try {
            liTarea = wait.until(d -> {
                Optional<WebElement> matching = d.findElements(By.cssSelector("ul.list-group li"))
                    .stream()
                    .filter(item -> item.getText().contains(tituloTarea))
                    .findFirst();
                return matching.orElse(null);
            });
            Assert.assertNotNull("Task not found after adding", liTarea);
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("DEBUG: Task not found after adding. DOM: " + driver.getPageSource());
            throw new AssertionError("Task not found within the timeout. Current DOM snapshot: " + driver.getPageSource(), e);
        }

        // Paso 5: Hacer clic en el botón de "Eliminar" junto a la tarea
        WebElement botonEliminar = liTarea.findElement(By.xpath(".//a[contains(@class,'btn-outline-danger')]"));
        botonEliminar.click();

        // Paso 6: Esperar que la tarea desaparezca de la lista
        try {
            wait.until(ExpectedConditions.stalenessOf(liTarea));
        } catch (TimeoutException e) {
            System.out.println("DEBUG: Task not removed after delete. DOM: " + driver.getPageSource());
            throw new AssertionError("Task was not removed from the DOM within the timeout. Current DOM snapshot: " + driver.getPageSource(), e);
        }

        // Paso 7: Verificar que la tarea ya no está visible en la lista
        Thread.sleep(1000);
        List<WebElement> itemsDespues = driver.findElements(By.cssSelector("ul.list-group li"));
        boolean tareaPresente = itemsDespues.stream().anyMatch(item -> item.getText().contains(tituloTarea));
        Assert.assertFalse("La tarea eliminada aún está presente en la lista", tareaPresente);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}