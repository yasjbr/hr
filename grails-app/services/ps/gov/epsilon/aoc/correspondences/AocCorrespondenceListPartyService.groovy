package ps.gov.epsilon.aoc.correspondences

import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass
import ps.gov.epsilon.aoc.lookups.Committee
import ps.gov.epsilon.aoc.lookups.CommitteeService
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.FirmService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO

@Transactional
class AocCorrespondenceListPartyService {

    OrganizationService organizationService
    FirmService firmService
    CommitteeService committeeService

    List searchWithRemotingValues(List<AocCorrespondenceListParty> aocCorrespondenceListPartyList, GrailsParameterMap params) {

        GrailsParameterMap searchParams = new GrailsParameterMap([:], null)

        List organizationParties= aocCorrespondenceListPartyList.findAll {it.partyClass==EnumCorrespondencePartyClass.ORGANIZATION}
        List firmParties= aocCorrespondenceListPartyList.findAll {it.partyClass==EnumCorrespondencePartyClass.FIRM}
        List committeeParties= aocCorrespondenceListPartyList.findAll {it.partyClass==EnumCorrespondencePartyClass.COMMITTEE}

        SearchBean searchBean = new SearchBean()
        if (!organizationParties?.isEmpty()) {
            /**
             * to organization name from core
             */
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: organizationParties.partyId))
            List<OrganizationDTO> organizationDTOList = organizationService?.searchOrganization(searchBean)?.resultList

            /**
             * assign organization name for each party in list
             */
            organizationParties?.each { AocCorrespondenceListParty aocCorrespondenceListParty ->
                aocCorrespondenceListParty?.transientData?.put("partyName", organizationDTOList?.find {
                    it?.id == aocCorrespondenceListParty?.partyId }?.descriptionInfo?.localName
                )
            }
        }
        if (!firmParties?.isEmpty()) {
            /**
             * to firm name
             */
            searchParams['ids[]']= firmParties?.partyId
            List<Firm> firmList = firmService.searchWithRemotingValues(searchParams)?.resultList

            /**
             * assign organization name for each party in list
             */
            firmParties?.each { AocCorrespondenceListParty aocCorrespondenceListParty ->
                aocCorrespondenceListParty?.transientData?.put("partyName", firmList?.find {
                    it?.id == aocCorrespondenceListParty?.partyId }?.name
                )
            }
        }
        if (!committeeParties?.isEmpty()) {
            /**
             * to Committee name
             */
            searchParams['ids[]']= committeeParties?.partyId
            List<Committee> committees = committeeService.search(searchParams)?.resultList

            /**
             * assign Committee name for each party in list
             */
            committeeParties?.each { AocCorrespondenceListParty aocCorrespondenceListParty ->
                aocCorrespondenceListParty?.transientData?.put("partyName", committees?.find {
                    it?.id == aocCorrespondenceListParty?.partyId }?.descriptionInfo?.localName
                )
            }
        }
//        aocCorrespondenceListPartyList?.each {
//            println("$it.partyId - $it.name")
//        }
        return aocCorrespondenceListPartyList
    }


}
