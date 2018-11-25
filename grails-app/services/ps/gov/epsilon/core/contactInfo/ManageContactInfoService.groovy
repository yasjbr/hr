package ps.gov.epsilon.core.contactInfo

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import ps.police.pcore.v2.entity.person.ContactInfoService
import ps.gov.epsilon.core.location.ManageLocationService
import ps.police.common.commands.v1.TrackingInfoCommand
import ps.police.pcore.enums.v1.ContactInfoClassification
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.ContactMethodCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.ContactTypeCommand
import ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand

import java.time.ZonedDateTime

@Transactional
class ManageContactInfoService {

    ContactInfoService contactInfoService
    SpringSecurityService springSecurityService
    ManageLocationService manageLocationService

    /**
     * save the Contact Info
     * @param params
     * @return
     */
    ContactInfoCommand saveContactInfo(GrailsParameterMap params) {
        ContactInfoCommand contactInfoCommand
        try {
            //create contact info's command object
            ContactInfoCommand applicantContactInfoCommand = new ContactInfoCommand()

            if (params.long("id")) {
                //update the Person Education
                applicantContactInfoCommand.id = params.long("id")
            }

            if (params.long("contactMethodId")) {
                ContactMethodCommand contactMethodCommand = new ContactMethodCommand(id: params.long("contactMethodId"))
                applicantContactInfoCommand.contactMethod = contactMethodCommand
            }



            if (params.long("contactTypeId")) {
                ContactTypeCommand contactTypeCommand = new ContactTypeCommand(id: params.long("contactTypeId"))
                applicantContactInfoCommand.contactType = contactTypeCommand
            }

            if (params.value) {
                String value = params.value as String
                applicantContactInfoCommand.value = value
            }

            if (params.long("personId")) {
                PersonCommand personCommand = new PersonCommand(id: params.long("personId"))
                applicantContactInfoCommand.person = personCommand
            }

            //tracking info for record in core's database
            TrackingInfoCommand trackingInfoCommand = new TrackingInfoCommand()
            trackingInfoCommand.sourceApplication = Holders.grailsApplication.config?.grails?.applicationName
            trackingInfoCommand.createdBy = springSecurityService?.principal?.username
            trackingInfoCommand.lastUpdatedBy = springSecurityService?.principal?.username
            trackingInfoCommand.dateCreatedUTC = ZonedDateTime.now()
            trackingInfoCommand.lastUpdatedUTC = ZonedDateTime.now()
            trackingInfoCommand.ipAddress = "localhost"

            applicantContactInfoCommand.trackingInfo = trackingInfoCommand
            applicantContactInfoCommand.relatedObjectType = ContactInfoClassification.PERSON


            if (params.long("governorateId")) {
                params.remove("id")
                if(params.long("locationId")){
                    params.id = params.locationId //edit location
                }
                LocationCommand locationCommand = manageLocationService.saveLocation(params)
                applicantContactInfoCommand.address = locationCommand
            }

            //save the contact info of applicant on core
            contactInfoCommand = contactInfoService?.saveContactInfo(applicantContactInfoCommand)
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            contactInfoCommand = new ContactInfoCommand()
            contactInfoCommand.errors.reject('default.not.created.message', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return contactInfoCommand
    }

}
