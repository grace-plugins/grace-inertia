package grace.demos

import org.springframework.validation.Errors

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ContactController {

    ContactService contactService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer page) {
        page = page ?: 1
        params.max = 10
        params.offset = (page - 1) * 10

        def contacts = contactService.list(params)
        def contactCount = contactService.count()
        def nextPage = page + 1
        def hasNext = (contactCount / 10) >= page
        def paginator = new Paginator('/contact/index', contactCount.intValue(), 10, page.intValue())
        def meta = new Meta(total: contactCount, pageSize: 10, pageNum: page, pageCount: paginator.getPageCount())
        render inertia: [data: contacts, pagination: paginator.pagination, meta: meta], viewData: [appName: 'CRM']
    }

    def show(Long id) {
        def contact = contactService.get(id)
        render inertia: [data: contact]
    }

    def create() {
        def contact = new Contact(params)
        render inertia: [data: contact]
    }

    def save(Contact contact) {
        if (contact == null) {
            notFound()
            return
        }

        try {
            contactService.save(contact)
        } catch (ValidationException e) {
            redirect inertia: [errors: validateErrors(contact.errors)], action: 'create'
            return
        }

        redirect contact
    }

    def edit(Long id) {
        def contact = contactService.get(id)
        render inertia: [data: contact]
    }

    def update(Contact contact) {
        if (contact == null) {
            notFound()
            return
        }

        try {
            contactService.save(contact)
        } catch (ValidationException e) {
            respond inertia: [errors: validateErrors(contact.errors)], view:'edit'
            return
        }

        request.withFormat {
            inertia {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contact.id])
                redirect inertia: [data: contact]
            }
            '*'{ respond contact, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        contactService.delete(id)

        request.withFormat {
            inertia {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    private Map mergeErrors(Map props, Errors errors) {
        def validateErrors = errors.fieldErrors.collectEntries {
            [it.field, g.message(error: it)]
        }
        props + [errors: validateErrors]
    }

    private Map validateErrors(Errors errors) {
        errors.fieldErrors.collectEntries {
            [it.field, g.message(error: it)]
        }
    }
}
