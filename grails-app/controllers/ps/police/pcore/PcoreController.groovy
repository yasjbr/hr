package ps.police.pcore

import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.lookups.AreaClassService
import ps.police.pcore.v2.entity.location.lookups.BlockService
import ps.police.pcore.v2.entity.location.lookups.BuildingService
import ps.police.pcore.v2.entity.location.lookups.CountryService
import ps.police.pcore.v2.entity.location.lookups.DistrictService
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.LocalityService
import ps.police.pcore.v2.entity.location.lookups.RegionService
import ps.police.pcore.v2.entity.location.lookups.StreetService
import ps.police.pcore.v2.entity.lookups.BloodTypeService
import ps.police.pcore.v2.entity.lookups.ColorService
import ps.police.pcore.v2.entity.lookups.CompetencyService
import ps.police.pcore.v2.entity.lookups.ContactMethodService
import ps.police.pcore.v2.entity.lookups.ContactTypeService
import ps.police.pcore.v2.entity.lookups.EducationDegreeService
import ps.police.pcore.v2.entity.lookups.EducationLevelService
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.lookups.EthnicityService
import ps.police.pcore.v2.entity.lookups.GenderTypeService
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.ProfessionTypeService
import ps.police.pcore.v2.entity.lookups.ReligionService
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.WorkingSectorService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.lookups.CorporationClassificationService
import ps.police.pcore.v2.entity.organization.lookups.OrganizationActivityService
import ps.police.pcore.v2.entity.organization.lookups.OrganizationTypeService
import ps.police.pcore.v2.entity.person.ContactInfoService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService

/**
 *<h1>Purpose</h1>
 * Route Pcore requests between model and views.
 *@see ps.police.pcore.v2.entity.lookups.EducationDegreeService
 **/

class PcoreController {


    EducationDegreeService educationDegreeService
    EducationMajorService educationMajorService
    GovernorateService governorateService
    ProfessionTypeService professionTypeService
    CompetencyService competencyService
    LocationService locationService
    ContactTypeService contactTypeService
    ContactMethodService contactMethodService
    RegionService regionService
    CountryService countryService
    DistrictService districtService
    LocalityService localityService
    BlockService blockService
    StreetService streetService
    BuildingService buildingService
    AreaClassService areaClassService
    PersonService personService
    PersonMaritalStatusService personMaritalStatusService
    ContactInfoService contactInfoService
    MaritalStatusService maritalStatusService
    OrganizationActivityService organizationActivityService
    OrganizationTypeService organizationTypeService
    WorkingSectorService workingSectorService
    CorporationClassificationService corporationClassificationService
    OrganizationService organizationService
    ColorService colorService
    EducationLevelService educationLevelService
    BloodTypeService bloodTypeService
    GenderTypeService genderTypeService
    ReligionService religionService
    EthnicityService ethnicityService


    def educationDegreeAutoComplete = {
        render text: (educationDegreeService.autoCompleteEducationDegree(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def educationMajorAutoComplete = {
        render text: (educationMajorService.autoCompleteEducationMajor(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def educationLevelAutoComplete = {
        render text: (educationLevelService.autoCompleteEducationLevel(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def governorateAutoComplete = {
        render text: (governorateService.autoCompleteGovernorate(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def professionTypeAutoComplete = {
        render text: (professionTypeService.autoCompleteProfessionType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def competencyAutoComplete = {
        render text: (competencyService.autoCompleteCompetency(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def locationAutoComplete = {
        render text: (locationService.autoCompleteLocation(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def contactTypeAutoComplete = {
        render text: (contactTypeService.autoCompleteContactType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def contactMethodAutoComplete = {
        render text: (contactMethodService.autoCompleteContactMethod(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def contactMethodPhoneAutoComplete = {
        render text: (contactMethodService.autoCompleteContactMethod(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def regionAutoComplete = {
        render text: (regionService.autoCompleteRegion(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def countryAutoComplete = {
        render text: (countryService.autoCompleteCountry(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def districtAutoComplete = {
        render text: (districtService.autoCompleteDistrict(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def localityAutoComplete = {
        render text: (localityService.autoCompleteLocality(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def blockAutoComplete = {
        render text: (blockService.autoCompleteBlock(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def streetAutoComplete = {
        render text: (streetService.autoCompleteStreet(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def buildingAutoComplete = {
        render text: (buildingService.autoCompleteBuilding(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def areaClassAutoComplete = {
        render text: (areaClassService.autoCompleteAreaClass(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def personClassAutoComplete = {
        render text: (personService.autoCompletePerson(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def searchPerson = {
        render text: (personService.searchPerson(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def getPerson = {
        render text: (personService.getPerson(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def searchPersonMaritalStatus = {
        render text: (personMaritalStatusService.searchPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def getPersonMaritalStatus = {
        render text: (personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def searchContactInfo = {
        render text: (contactInfoService.searchContactInfo(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def maritalStatusAutoComplete = {
        render text: (maritalStatusService.autoCompleteMaritalStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def organizationActivityAutoComplete = {
        render text: (organizationActivityService.autoCompleteOrganizationActivity(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def organizationTypeAutoComplete = {
        render text: (organizationTypeService.autoCompleteOrganizationType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def workingSectorAutoComplete = {
        render text: (workingSectorService.autoCompleteWorkingSector(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def corporationClassificationAutoComplete = {
        render text: (corporationClassificationService.autoCompleteCorporationClassification(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def organizationAutoComplete={
        render text: (organizationService.autoCompleteOrganization(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def colorAutoComplete = {
        render text: (colorService.autoCompleteColor(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def bloodTypeAutoComplete = {
        render text: (bloodTypeService.autoCompleteBloodType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def genderTypeAutoComplete = {
        render text: (genderTypeService.autoCompleteGenderType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def religionAutoComplete = {
        render text: (religionService.autoCompleteReligion(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    def ethnicityAutoComplete = {
        render text: (ethnicityService.autoCompleteEthnicity(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}
