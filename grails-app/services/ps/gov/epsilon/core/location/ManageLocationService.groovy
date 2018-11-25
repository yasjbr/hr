package ps.gov.epsilon.core.location

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.police.pcore.v2.entity.location.lookups.CountryService
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.lookups.commands.v1.*
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.CountryDTO

@Transactional
class ManageLocationService {

    LocationService locationService
    CountryService countryService

    /**
     * return the location command
     * @param params
     * @return
     */
    LocationCommand getLocationCommand(GrailsParameterMap params) {
        LocationCommand locationCommand

        try {
            //create country command, country is required field in location :
            if (params.long("governorateId") || params.long("countryId")){
                locationCommand = new LocationCommand();
                //create location's command object

                if (params.long("countryId")) {
                    CountryCommand countryCommand = new CountryCommand(id: params.long("countryId"))
                    locationCommand.country = countryCommand
                } else {
                    SearchBean searchBean = new SearchBean()
                    searchBean.searchCriteria.put("descriptionInfo.localName", new SearchConditionCriteriaBean(operand: "descriptionInfo.localName", value1: "فلسطين"))
                    CountryDTO country = countryService.getCountry(searchBean)
                    CountryCommand countryCommand = new CountryCommand(id: country.id)
                    locationCommand.country = countryCommand
                }

                //create region command
                if (params.long("regionId")) {
                    RegionCommand regionCommand = new RegionCommand(id: params.long("regionId"))
                    locationCommand.region = regionCommand
                }

                //create district command
                if (params.long("districtId")) {
                    DistrictCommand districtCommand = new DistrictCommand(id: params.long("districtId"))
                    locationCommand.district = districtCommand
                }

                //create governorate command:
                if (params.long("governorateId")) {
                    GovernorateCommand governorateCommand = new GovernorateCommand(id: params.long("governorateId"))
                    locationCommand.governorate = governorateCommand
                }

                //create locality command:
                if (params.long("localityId")) {
                    LocalityCommand localityCommand = new LocalityCommand(id: params.long("localityId"))
                    locationCommand.locality = localityCommand
                }

                //create block command:
                if (params.long("blockId")) {
                    BlockCommand blockCommand = new BlockCommand(id: params.long("blockId"))
                    locationCommand.block = blockCommand
                }

                //create street command:
                if (params.long("streetId")) {
                    StreetCommand streetCommand = new StreetCommand(id: params.long("streetId"))
                    locationCommand.street = streetCommand
                }

                ////create building command:
                if (params.long("buildingId")) {
                    BuildingCommand buildingCommand = new BuildingCommand(id: params.long("buildingId"))
                    locationCommand.building = buildingCommand
                }

                //create area command:
                if (params.long("areaClassId")) {
                    AreaClassCommand areaClassCommand = new AreaClassCommand(id: params.long("areaClassId"))
                    locationCommand.areaClass = areaClassCommand
                }
            }
            return locationCommand
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    /**
     * return the location command
     * @param params
     * @return
     */
    LocationCommand getLocationCommand2(GrailsParameterMap params) {
        LocationCommand locationCommand

        try {
            //create country command, country is required field in location :
            if (params.long("country.id")) {
                locationCommand = new LocationCommand();
                //create location's command object

                if (params.long("country.id")) {
                    CountryCommand countryCommand = new CountryCommand(id: params.long("country.id"))
                    locationCommand.country = countryCommand
                }

                //create region command
                if (params.long("region.id")) {
                    RegionCommand regionCommand = new RegionCommand(id: params.long("region.id"))
                    locationCommand.region = regionCommand
                }

                //create district command
                if (params.long("district.id")) {
                    DistrictCommand districtCommand = new DistrictCommand(id: params.long("district.id"))
                    locationCommand.district = districtCommand
                }

                //create governorate command:
                if (params.long("governorate.id")) {
                    GovernorateCommand governorateCommand = new GovernorateCommand(id: params.long("governorate.id"))
                    locationCommand.governorate = governorateCommand
                }

                //create locality command:
                if (params.long("locality.id")) {
                    LocalityCommand localityCommand = new LocalityCommand(id: params.long("locality.id"))
                    locationCommand.locality = localityCommand
                }

                //create block command:
                if (params.long("block.id")) {
                    BlockCommand blockCommand = new BlockCommand(id: params.long("block.id"))
                    locationCommand.block = blockCommand
                }

                //create street command:
                if (params.long("street.id")) {
                    StreetCommand streetCommand = new StreetCommand(id: params.long("street.id"))
                    locationCommand.street = streetCommand
                }

                ////create building command:
                if (params.long("building.id")) {
                    BuildingCommand buildingCommand = new BuildingCommand(id: params.long("building.id"))
                    locationCommand.building = buildingCommand
                }

                //create area command:
                if (params.long("areaClass.id")) {
                    AreaClassCommand areaClassCommand = new AreaClassCommand(id: params.long("areaClass.id"))
                    locationCommand.areaClass = areaClassCommand
                }
            }
            return locationCommand
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    /**
     * save the location remotely on core
     * @param params
     * @return LocationCommand obj
     */
    LocationCommand saveLocation(GrailsParameterMap params) {
        LocationCommand locationCommand
        try {
            if(params.withWrapper){
                locationCommand = getLocationCommand2(params);
            }else {
                locationCommand = getLocationCommand(params);
            }

            //save the location in core:
            if (locationCommand.validate()) {
                locationCommand = locationService.saveLocation(locationCommand)
            } else {
                throw new Exception(locationCommand.errors)
            }

        } catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            ex.printStackTrace()
            locationCommand = new LocationCommand()
            locationCommand.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")

        }
        return locationCommand
    }
}
