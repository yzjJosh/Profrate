import jinja2


# Render a template from a template file name and a parameters dict
def renderTemplate(name, parameters):
    JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)
    return JINJA_ENVIRONMENT.get_template(name).render(parameters)