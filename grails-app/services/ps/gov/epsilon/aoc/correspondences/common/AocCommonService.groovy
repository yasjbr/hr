package ps.gov.epsilon.aoc.correspondences.common

import grails.transaction.Transactional
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

@Transactional
class AocCommonService {

    PersonService personService
    GovernorateService governorateService
    OrganizationService organizationService

    /**
     * get PersonDTO from core using personId.
     * @param personIds.
     * @return personDTOList.
     */
    public List<PersonDTO> searchPersonData(List<Long> personIds) {
        SearchBean searchBean
        List<PersonDTO> personDTOList = null

        if (personIds?.size() > 0) {
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: personIds))
            personDTOList = personService?.searchPerson(searchBean)?.resultList
        }
        return personDTOList
    }

    /**
     * get OrganizationDTO  from CORE using id.
     * @param organizationIds.
     * @return organizationDTOList.
     */
    public List<OrganizationDTO> searchOrganizationData(List<Long> organizationIds) {
        SearchBean searchBean
        List<OrganizationDTO> organizationDTOList = null

        if (organizationIds?.size() > 0) {
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: organizationIds))
            organizationDTOList = organizationService?.searchOrganization(searchBean)?.resultList
        }
        return organizationDTOList
    }

    /**
     * get OrganizationDTO  from CORE using id.
     * @param governorateIds.
     * @return governorateDTOList.
     */
    public List<GovernorateDTO> searchGovernoratesData(List<Long> governorateIds) {
        List<GovernorateDTO> governorateDTOList = null
        if (governorateIds?.size() > 0) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: governorateIds))
            governorateDTOList = governorateService.searchGovernorate(searchBean)?.resultList
        }
        return governorateDTOList
    }

}
