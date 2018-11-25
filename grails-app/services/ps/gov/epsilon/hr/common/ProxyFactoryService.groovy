package ps.gov.epsilon.hr.common

import grails.transaction.Transactional
import grails.util.Holders
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import ps.gov.epsilon.remoting.SimpleHttpInvokerProxyFactoryBean

@Transactional
class ProxyFactoryService implements ApplicationContextAware {

    def applicationContext

    final String CORE_RPC = Holders.grailsApplication.config.remoting.CORE_URL
    final String SERVICE_CATALOG_RPC = Holders.grailsApplication.config.remoting.SERVICE_CATALOG_URL

    final String APPLICATION_KEY = Holders.grailsApplication.config.grails.applicationKey

    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }


    def userProxy

    public void userProxySetup() {
        // This is for the service catalog.
        if (!userProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerUser = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerUser.setServiceInterface(ps.police.security.interfaces.v1.IUser)
                httpInvokerUser.setServiceUrl(SERVICE_CATALOG_RPC + "/httpinvoker/RemoteUserService")
                httpInvokerUser.afterPropertiesSet()
                userProxy = (ps.police.security.interfaces.v1.IUser) httpInvokerUser.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def legalIdentifierProxy

    public void legalIdentifierProxySetup() {
        // This is for the core.
        if (!legalIdentifierProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifier)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLegalIdentifierService")
                httpInvokerEmployee.afterPropertiesSet()
                legalIdentifierProxy = (ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifier) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def legalIdentifierLevelProxy

    public void legalIdentifierLevelProxySetup() {
        // This is for the core.
        if (!legalIdentifierLevelProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifierLevel)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLegalIdentifierLevelService")
                httpInvokerEmployee.afterPropertiesSet()
                legalIdentifierLevelProxy = (ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifierLevel) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def legalIdentifierRestrictionProxy

    public void legalIdentifierRestrictionProxySetup() {
        // This is for the core.
        if (!legalIdentifierRestrictionProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifierRestriction)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLegalIdentifierRestrictionService")
                httpInvokerEmployee.afterPropertiesSet()
                legalIdentifierRestrictionProxy = (ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifierRestriction) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def personProxy

    public void personProxySetup() {
        // This is for the core.
        if (!personProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPerson)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonService")
                httpInvokerEmployee.afterPropertiesSet()
                personProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPerson) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def documentTypeProxy

    public void documentTypeProxySetup() {
        // This is for the core.
        if (!documentTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IDocumentType)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteDocumentTypeService")
                httpInvokerEmployee.afterPropertiesSet()
                documentTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IDocumentType) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def documentClassificationProxy

    public void documentClassificationProxySetup() {
        // This is for the core.
        if (!documentClassificationProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IDocumentClassification)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteDocumentClassificationService")
                httpInvokerEmployee.afterPropertiesSet()
                documentClassificationProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IDocumentClassification) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personMaritalStatusProxy

    public void personMaritalStatusProxySetup() {
        // This is for the core.
        if (!personMaritalStatusProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEmployee = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEmployee.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonMaritalStatus)
                httpInvokerEmployee.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonMaritalStatusService")
                httpInvokerEmployee.afterPropertiesSet()
                personMaritalStatusProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonMaritalStatus) httpInvokerEmployee.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def organizationProxy

    public void organizationProxySetup() {
        // This is for the core.
        if (!organizationProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerOrganization = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerOrganization.setServiceInterface(ps.police.pcore.v2.entity.organization.interfaces.v1.IOrganization)
                httpInvokerOrganization.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteOrganizationService")
                httpInvokerOrganization.afterPropertiesSet()
                organizationProxy = (ps.police.pcore.v2.entity.organization.interfaces.v1.IOrganization) httpInvokerOrganization.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def educationDegreeProxy

    public void educationDegreeProxySetup() {
        // This is for the core.
        if (!educationDegreeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEducationDegree = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEducationDegree.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationDegree)
                httpInvokerEducationDegree.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteEducationDegreeService")
                httpInvokerEducationDegree.afterPropertiesSet()
                educationDegreeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationDegree) httpInvokerEducationDegree.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def educationMajorProxy

    public void educationMajorProxySetup() {
        // This is for the core.
        if (!educationMajorProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEducationMajor = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEducationMajor.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationMajor)
                httpInvokerEducationMajor.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteEducationMajorService")
                httpInvokerEducationMajor.afterPropertiesSet()
                educationMajorProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationMajor) httpInvokerEducationMajor.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def educationLevelProxy

    public void educationLevelProxySetup() {
        // This is for the core.
        if (!educationLevelProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEducationLevel = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEducationLevel.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationLevel)
                httpInvokerEducationLevel.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteEducationLevelService")
                httpInvokerEducationLevel.afterPropertiesSet()
                educationLevelProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationLevel) httpInvokerEducationLevel.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personEducationProxy

    public void personEducationProxySetup() {
        // This is for the core.
        if (!personEducationProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerPersonEducation = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerPersonEducation.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonEducation)
                httpInvokerPersonEducation.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonEducationService")
                httpInvokerPersonEducation.afterPropertiesSet()
                personEducationProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonEducation) httpInvokerPersonEducation.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def governorateProxy

    public void governorateProxySetup() {
        // This is for the core.
        if (!governorateProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerGovernorate = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerGovernorate.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IGovernorate)
                httpInvokerGovernorate.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteGovernorateService")
                httpInvokerGovernorate.afterPropertiesSet()
                governorateProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IGovernorate) httpInvokerGovernorate.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def professionTypeProxy

    public void professionTypeProxySetup() {
        // This is for the core.
        if (!professionTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerProfessionType = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerProfessionType.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IProfessionType)
                httpInvokerProfessionType.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteProfessionTypeService")
                httpInvokerProfessionType.afterPropertiesSet()
                professionTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IProfessionType) httpInvokerProfessionType.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def competencyProxy

    public void competencyProxySetup() {
        // This is for the core.
        if (!competencyProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerCompetency = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerCompetency.setServiceInterface(ps.police.pcore.v2.entity.person.lookups.interfaces.v1.ICompetency)
                httpInvokerCompetency.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteCompetencyService")
                httpInvokerCompetency.afterPropertiesSet()
                competencyProxy = (ps.police.pcore.v2.entity.person.lookups.interfaces.v1.ICompetency) httpInvokerCompetency.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def locationProxy

    public void locationProxySetup() {
        // This is for the core.
        if (!locationProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerLocation = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerLocation.setServiceInterface(ps.police.pcore.v2.entity.location.interfaces.v1.ILocation)
                httpInvokerLocation.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLocationService")
                httpInvokerLocation.afterPropertiesSet()
                locationProxy = (ps.police.pcore.v2.entity.location.interfaces.v1.ILocation) httpInvokerLocation.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def contactTypeProxy

    public void contactTypeProxySetup() {
        // This is for the core.
        if (!contactTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerContactType = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerContactType.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IContactType)
                httpInvokerContactType.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteContactTypeService")
                httpInvokerContactType.afterPropertiesSet()
                contactTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IContactType) httpInvokerContactType.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def contactMethodProxy

    public void contactMethodProxySetup() {
        // This is for the core.
        if (!contactMethodProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerContactMethod = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerContactMethod.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IContactMethod)
                httpInvokerContactMethod.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteContactMethodService")
                httpInvokerContactMethod.afterPropertiesSet()
                contactMethodProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IContactMethod) httpInvokerContactMethod.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def regionProxy

    public void regionProxySetup() {
        // This is for the core.
        if (!regionProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerRegion = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerRegion.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IRegion)
                httpInvokerRegion.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteRegionService")
                httpInvokerRegion.afterPropertiesSet()
                regionProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IRegion) httpInvokerRegion.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def streetProxy

    public void streetProxySetup() {
        // This is for the core.
        if (!streetProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerStreet = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerStreet.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IStreet)
                httpInvokerStreet.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteStreetService")
                httpInvokerStreet.afterPropertiesSet()
                streetProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IStreet) httpInvokerStreet.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def localityProxy

    public void localityProxySetup() {
        // This is for the core.
        if (!localityProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerLocality = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerLocality.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.ILocality)
                httpInvokerLocality.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLocalityService")
                httpInvokerLocality.afterPropertiesSet()
                localityProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.ILocality) httpInvokerLocality.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }
    def districtProxy

    public void districtProxySetup() {
        // This is for the core.
        if (!districtProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerDistrict = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerDistrict.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IDistrict)
                httpInvokerDistrict.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteDistrictService")
                httpInvokerDistrict.afterPropertiesSet()
                districtProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IDistrict) httpInvokerDistrict.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }
    def countryProxy

    public void countryProxySetup() {
        // This is for the core.
        if (!countryProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerCountry = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerCountry.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.ICountry)
                httpInvokerCountry.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteCountryService")
                httpInvokerCountry.afterPropertiesSet()
                countryProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.ICountry) httpInvokerCountry.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }
    def buildingProxy

    public void buildingProxySetup() {
        // This is for the core.
        if (!buildingProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerBuilding = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerBuilding.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IBuilding)
                httpInvokerBuilding.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteBuildingService")
                httpInvokerBuilding.afterPropertiesSet()
                buildingProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IBuilding) httpInvokerBuilding.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def blockProxy

    public void blockProxySetup() {
        // This is for the core.
        if (!blockProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerBlock = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerBlock.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IBlock)
                httpInvokerBlock.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteBlockService")
                httpInvokerBlock.afterPropertiesSet()
                blockProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IBlock) httpInvokerBlock.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def areaClassProxy

    public void areaClassProxySetup() {
        // This is for the core.
        if (!areaClassProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerAreaClass = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerAreaClass.setServiceInterface(ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IAreaClass)
                httpInvokerAreaClass.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteAreaClassService")
                httpInvokerAreaClass.afterPropertiesSet()
                areaClassProxy = (ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IAreaClass) httpInvokerAreaClass.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def contactInfoProxy

    public void contactInfoProxySetup() {
        // This is for the core.
        if (!contactInfoProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerContactInfo = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerContactInfo.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IContactInfo)
                httpInvokerContactInfo.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteContactInfoService")
                httpInvokerContactInfo.afterPropertiesSet()
                contactInfoProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IContactInfo) httpInvokerContactInfo.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def maritalStatusProxy

    public void maritalStatusProxySetup() {
        // This is for the core.
        if (!maritalStatusProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerMaritalStatus = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerMaritalStatus.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IMaritalStatus)
                httpInvokerMaritalStatus.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteMaritalStatusService")
                httpInvokerMaritalStatus.afterPropertiesSet()
                maritalStatusProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IMaritalStatus) httpInvokerMaritalStatus.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }
    def organizationActivityProxy

    public void organizationActivityProxySetup() {
        // This is for the core.
        if (!organizationActivityProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerOrganizationActivity = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerOrganizationActivity.setServiceInterface(ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.IOrganizationActivity)
                httpInvokerOrganizationActivity.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteOrganizationActivityService")
                httpInvokerOrganizationActivity.afterPropertiesSet()
                organizationActivityProxy = (ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.IOrganizationActivity) httpInvokerOrganizationActivity.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }
    def organizationTypeProxy

    public void organizationTypeProxySetup() {
        // This is for the core.
        if (!organizationTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerOrganizationType = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerOrganizationType.setServiceInterface(ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.IOrganizationType)
                httpInvokerOrganizationType.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteOrganizationTypeService")
                httpInvokerOrganizationType.afterPropertiesSet()
                organizationTypeProxy = (ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.IOrganizationType) httpInvokerOrganizationType.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def workingSectorProxy

    public void workingSectorProxySetup() {
        // This is for the core.
        if (!workingSectorProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerWorkingSector = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerWorkingSector.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IWorkingSector)
                httpInvokerWorkingSector.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteWorkingSectorService")
                httpInvokerWorkingSector.afterPropertiesSet()
                workingSectorProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IWorkingSector) httpInvokerWorkingSector.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def corporationClassificationProxy

    public void corporationClassificationProxySetup() {
        // This is for the core.
        if (!corporationClassificationProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerCorporationClassification = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerCorporationClassification.setServiceInterface(ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.ICorporationClassification)
                httpInvokerCorporationClassification.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteCorporationClassificationService")
                httpInvokerCorporationClassification.afterPropertiesSet()
                corporationClassificationProxy = (ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.ICorporationClassification) httpInvokerCorporationClassification.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def colorProxy

    public void colorProxySetup() {
        // This is for the core.
        if (!colorProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerColor = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerColor.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IColor)
                httpInvokerColor.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteColorService")
                httpInvokerColor.afterPropertiesSet()
                colorProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IColor) httpInvokerColor.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def bloodTypeProxy

    public void bloodTypeProxySetup() {
        // This is for the core.
        if (!bloodTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerBloodType = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerBloodType.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IBloodType)
                httpInvokerBloodType.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteBloodTypeService")
                httpInvokerBloodType.afterPropertiesSet()
                bloodTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IBloodType) httpInvokerBloodType.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def genderTypeProxy

    public void genderTypeProxySetup() {
        // This is for the core.
        if (!genderTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerGenderType = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerGenderType.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IGenderType)
                httpInvokerGenderType.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteGenderTypeService")
                httpInvokerGenderType.afterPropertiesSet()
                genderTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IGenderType) httpInvokerGenderType.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def religionProxy

    public void religionProxySetup() {
        // This is for the core.
        if (!religionProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerReligion = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerReligion.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IReligion)
                httpInvokerReligion.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteReligionService")
                httpInvokerReligion.afterPropertiesSet()
                religionProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IReligion) httpInvokerReligion.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def ethnicityProxy

    public void ethnicityProxySetup() {
        // This is for the core.
        if (!ethnicityProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerEthnicity = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerEthnicity.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IEthnicity)
                httpInvokerEthnicity.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteEthnicityService")
                httpInvokerEthnicity.afterPropertiesSet()
                ethnicityProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IEthnicity) httpInvokerEthnicity.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def personEmploymentHistoryProxy

    public void personEmploymentHistoryProxySetup() {
        // This is for the core.
        if (!personEmploymentHistoryProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerPersonEmploymentHistory = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerPersonEmploymentHistory.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonEmploymentHistory)
                httpInvokerPersonEmploymentHistory.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonEmploymentHistoryService")
                httpInvokerPersonEmploymentHistory.afterPropertiesSet()
                personEmploymentHistoryProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonEmploymentHistory) httpInvokerPersonEmploymentHistory.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personArrestHistoryProxy

    public void personArrestHistoryProxySetup() {
        // This is for the core.
        if (!personArrestHistoryProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerPersonArrestHistory = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerPersonArrestHistory.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonArrestHistory)
                httpInvokerPersonArrestHistory.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonArrestHistoryService")
                httpInvokerPersonArrestHistory.afterPropertiesSet()
                personArrestHistoryProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonArrestHistory) httpInvokerPersonArrestHistory.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personCharacteristicsProxy

    public void personCharacteristicsProxySetup() {
        // This is for the core.
        if (!personCharacteristicsProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvokerPersonArrestHistory = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvokerPersonArrestHistory.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonCharacteristics)
                httpInvokerPersonArrestHistory.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonCharacteristicsService")
                httpInvokerPersonArrestHistory.afterPropertiesSet()
                personCharacteristicsProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonCharacteristics) httpInvokerPersonArrestHistory.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personHealthHistoryProxy

    public void personHealthHistoryProxySetup() {
        // This is for the core.
        if (!personHealthHistoryProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonHealthHistory)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonHealthHistoryService")
                httpInvoker.afterPropertiesSet()
                personHealthHistoryProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonHealthHistory) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personCountryVisitProxy

    public void personCountryVisitProxySetup() {
        // This is for the core.
        if (!personCountryVisitProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonCountryVisit)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonCountryVisitService")
                httpInvoker.afterPropertiesSet()
                personCountryVisitProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonCountryVisit) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def personDisabilityInfoProxy

    public void personDisabilityInfoProxySetup() {
        // This is for the core.
        if (!personDisabilityInfoProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonDisabilityInfo)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonDisabilityInfoService")
                httpInvoker.afterPropertiesSet()
                personDisabilityInfoProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonDisabilityInfo) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personLanguageInfoProxy

    public void personLanguageInfoProxySetup() {
        // This is for the core.
        if (!personLanguageInfoProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonLanguageInfo)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonLanguageInfoService")
                httpInvoker.afterPropertiesSet()
                personLanguageInfoProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonLanguageInfo) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personLiveStatusProxy

    public void personLiveStatusProxySetup() {
        // This is for the core.
        if (!personLiveStatusProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonLiveStatus)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonLiveStatusService")
                httpInvoker.afterPropertiesSet()
                personLiveStatusProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonLiveStatus) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personNationalityProxy

    public void personNationalityProxySetup() {
        // This is for the core.
        if (!personNationalityProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonNationality)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonNationalityService")
                httpInvoker.afterPropertiesSet()
                personNationalityProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonNationality) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def personRelationShipsProxy

    public void personRelationShipsProxySetup() {
        // This is for the core.
        if (!personRelationShipsProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonRelationShips)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonRelationShipsService")
                httpInvoker.afterPropertiesSet()
                personRelationShipsProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonRelationShips) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def personTrainingHistoryProxy

    public void personTrainingHistoryProxySetup() {
        // This is for the core.
        if (!personTrainingHistoryProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.person.interfaces.v1.IPersonTrainingHistory)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemotePersonTrainingHistoryService")
                httpInvoker.afterPropertiesSet()
                personTrainingHistoryProxy = (ps.police.pcore.v2.entity.person.interfaces.v1.IPersonTrainingHistory) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def jailProxy

    public void jailProxySetup() {
        // This is for the core.
        if (!jailProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IJail)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteJailService")
                httpInvoker.afterPropertiesSet()
                jailProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IJail) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }
    def borderCrossingPointProxy

    public void borderCrossingPointProxySetup() {
        // This is for the core.
        if (!borderCrossingPointProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IBorderCrossingPoint)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteBorderCrossingPointService")
                httpInvoker.afterPropertiesSet()
                borderCrossingPointProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IBorderCrossingPoint) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def relationshipTypeProxy

    public void relationshipTypeProxySetup() {
        // This is for the core.
        if (!relationshipTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IRelationshipType)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteRelationshipTypeService")
                httpInvoker.afterPropertiesSet()
                relationshipTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IRelationshipType) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def languageProxy

    public void languageProxySetup() {
        // This is for the core.
        if (!languageProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.ILanguage)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLanguageService")
                httpInvoker.afterPropertiesSet()
                languageProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.ILanguage) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def trainingCategoryProxy

    public void trainingCategoryProxySetup() {
        // This is for the core.
        if (!trainingCategoryProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.ITrainingCategory)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteTrainingCategoryService")
                httpInvoker.afterPropertiesSet()
                trainingCategoryProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.ITrainingCategory) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def trainingDegreeProxy

    public void trainingDegreeProxySetup() {
        // This is for the core.
        if (!trainingDegreeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.ITrainingDegree)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteTrainingDegreeService")
                httpInvoker.afterPropertiesSet()
                trainingDegreeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.ITrainingDegree) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def unitOfMeasurementProxy

    public void unitOfMeasurementProxySetup() {
        // This is for the core.
        if (!unitOfMeasurementProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IUnitOfMeasurement)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteUnitOfMeasurementService")
                httpInvoker.afterPropertiesSet()
                unitOfMeasurementProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IUnitOfMeasurement) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def nationalityAcquisitionMethodProxy

    public void nationalityAcquisitionMethodProxySetup() {
        // This is for the core.
        if (!nationalityAcquisitionMethodProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.INationalityAcquisitionMethod)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteNationalityAcquisitionMethodService")
                httpInvoker.afterPropertiesSet()
                nationalityAcquisitionMethodProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.INationalityAcquisitionMethod) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }

    def hairFeatureProxy

    public void hairFeatureProxySetup() {
        // This is for the core.
        if (!hairFeatureProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IHairFeature)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteHairFeatureService")
                httpInvoker.afterPropertiesSet()
                hairFeatureProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IHairFeature) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def diseaseTypeProxy

    public void diseaseTypeProxySetup() {
        // This is for the core.
        if (!diseaseTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IDiseaseType)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteDiseaseTypeService")
                httpInvoker.afterPropertiesSet()
                diseaseTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IDiseaseType) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def disabilityLevelProxy

    public void disabilityLevelProxySetup() {
        // This is for the core.
        if (!disabilityLevelProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IDisabilityLevel)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteDisabilityLevelService")
                httpInvoker.afterPropertiesSet()
                disabilityLevelProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IDisabilityLevel) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def disabilityTypeProxy

    public void disabilityTypeProxySetup() {
        // This is for the core.
        if (!disabilityTypeProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.IDisabilityType)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteDisabilityTypeService")
                httpInvoker.afterPropertiesSet()
                disabilityTypeProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.IDisabilityType) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def liveStatusProxy

    public void liveStatusProxySetup() {
        // This is for the core.
        if (!liveStatusProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.ILiveStatus)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteLiveStatusService")
                httpInvoker.afterPropertiesSet()
                liveStatusProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.ILiveStatus) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


    def currencyProxy

    public void currencyProxySetup() {
        // This is for the core.
        if (!currencyProxy) {
            try {
                SimpleHttpInvokerProxyFactoryBean httpInvoker = new SimpleHttpInvokerProxyFactoryBean(APPLICATION_KEY)
                httpInvoker.setServiceInterface(ps.police.pcore.v2.entity.lookups.interfaces.v1.ICurrency)
                httpInvoker.setServiceUrl(CORE_RPC + "/httpinvoker/RemoteCurrencyService")
                httpInvoker.afterPropertiesSet()
                currencyProxy = (ps.police.pcore.v2.entity.lookups.interfaces.v1.ICurrency) httpInvoker.getObject()
            } catch (ConnectException ex) {
                log.error("Failed to connect to the Server", ex)
            }
        }
    }


}
