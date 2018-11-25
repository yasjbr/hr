package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EndorseOrder requests between model and views.
 *@see EndorseOrderService
 *@see FormatService
**/
class EndorseOrderController  {

    EndorseOrderService endorseOrderService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    /**
     * default action in controller
     */
    def index= {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list= {}

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId || params.endorseOrderEncodedId){
            if(params.endorseOrderEncodedId) {
                params.encodedId = params.remove("endorseOrderEncodedId")
            }
            EndorseOrder endorseOrder = endorseOrderService.getInstance(params)
            if(endorseOrder){
                respond endorseOrder
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {

        println("params: $params")

        if(params['loanNominatedEmployeeEncodedId']){
            EndorseOrder endorseOrder = new EndorseOrder(params)
            Map map = [loanNominatedEmployeeEncodedId:params['loanNominatedEmployeeEncodedId'],endorseOrder:endorseOrder]
            respond map
        }else{
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = endorseOrderService.search(params)
        render text: (endorseOrderService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EndorseOrder endorseOrder = endorseOrderService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'endorseOrder.entity', default: 'EndorseOrder'), endorseOrder?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'endorseOrder.entity', default: 'EndorseOrder'), endorseOrder?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(endorseOrder, successMessage, failMessage, true, "loanNominatedEmployee","list") as JSON), contentType: "application/json"
        }
        else {
            if (endorseOrder?.hasErrors()) {
                respond endorseOrder, view:'create'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(controller:"loanNominatedEmployee",action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId || params.endorseOrderEncodedId){
            if(params.endorseOrderEncodedId) {
                params.encodedId = params.remove("endorseOrderEncodedId")
                //in case get from loan nominated employee
                params.withLoanNominatedEmployee = "true"
            }
            EndorseOrder endorseOrder = endorseOrderService.getInstance(params)
            if(endorseOrder){
                if( endorseOrder?.transientData?.loanNominatedEmployee &&  endorseOrder?.transientData?.loanNominatedEmployee?.loanNoticeReplayRequest?.loanNotice?.loanNoticeStatus != EnumLoanNoticeStatus.DONE_NOMINATION){
                    String failMessage = message(code: 'endorseOrder.failEdit.label',default: 'fail edit')
                    flash.message = msg.error(label: failMessage)
                    redirect(controller: "loanNominatedEmployee",action: "list")
                }else{
                    Map map = [loanNominatedEmployeeEncodedId:params['loanNominatedEmployeeEncodedId'],endorseOrder:endorseOrder]
                    respond map
                }
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        EndorseOrder endorseOrder = endorseOrderService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'endorseOrder.entity', default: 'EndorseOrder'), endorseOrder?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'endorseOrder.entity', default: 'EndorseOrder'), endorseOrder?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(endorseOrder, successMessage, failMessage, true, "loanNominatedEmployee","list") as JSON), contentType: "application/json"
        }
        else {
            if (endorseOrder.hasErrors()) {
                respond endorseOrder, view:'edit'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(controller:"loanNominatedEmployee",action: "list")
            }
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'endorseOrder.entity', default: 'EndorseOrder'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

