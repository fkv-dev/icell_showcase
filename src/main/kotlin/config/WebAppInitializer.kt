package hu.fkv.config

import jakarta.servlet.ServletContext
import jakarta.servlet.ServletRegistration
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

class WebAppInitializer : WebApplicationInitializer {
    override fun onStartup(servletContext: ServletContext) {
        val ctx = AnnotationConfigWebApplicationContext()
        ctx.register(WebConfig::class.java)
        ctx.servletContext = servletContext

        val servlet: ServletRegistration.Dynamic = servletContext.addServlet("dispatcher", DispatcherServlet(ctx))
        servlet.setLoadOnStartup(1)
        servlet.addMapping("/")
    }
}
