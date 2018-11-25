package ps.gov.epsilon.hr.firm.loan

import grails.artefact.TagLibrary
import grails.gsp.TagLib
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource

@TagLib
class LoanTagLib implements TagLibrary {

    static namespace = "loan"

    LoanRequestService loanRequestService

    /**
     * using to get received loan person.
     * @attr id
     */
    def getReceivedLoanPerson = { attrs, body ->
        String id = attrs.remove("id") as String
        LoanListPerson loanListPerson = LoanListPerson.load(id)
        LoanRequest loanRequest = loanRequestService.getInstanceWithRemotingValues(new GrailsParameterMap([id:loanListPerson?.loanRequest?.id],request))
        String html = ""
        String script = ""
        loanRequest?.loanRequestRelatedPersons?.toList()?.each { LoanRequestRelatedPerson relatedPerson ->
            if (relatedPerson.recordSource == EnumPersonSource.RECEIVED) {
                html += """   <li id="relatedPerson_${relatedPerson?.requestedPersonId}" class="well-sm alert alert-success"> """
                html += """   <span id="relatedPerson_${relatedPerson?.requestedPersonId}_span">  """
                html += """   ${relatedPerson?.transientData?.requestedPersonDTO}  """
                html += """   <input type="hidden" value="${relatedPerson?.requestedPersonId}" name="receivedPersonId">  """
                html += """   </span>  """
                html += """   <button class='close' href='#' data-dismiss='alert'>X</button>  """
                html += """   </li>  """

                script += """ _currentValues.push("${relatedPerson?.transientData?.requestedPersonDTO}"); """
                script += """ _spanIds.push("relatedPerson_${relatedPerson?.requestedPersonId}_span"); """
            }
        }

        out << html
        out << """ <script>${script}</script> """
    }

}
