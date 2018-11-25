package ps.police.pcore.v2.entity.location

import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.person.dtos.v1.ContactInfoDTO

/**
 * Created by hatallah on 04/06/17.
 */
class LocationAddressUtil {
    /**
     * return the address details for any contact info object
     * @param contactInfoDTO
     * @return string
     */
    public static String renderLocation(LocationDTO locationDTO, String value){
        String address = ""
        if (locationDTO) {
//            if(locationDTO?.region)
//              address += locationDTO?.region?.descriptionInfo?.localName + " - "

            if(locationDTO?.country)
                address += locationDTO?.country?.descriptionInfo?.localName + " - "

            if(locationDTO?.district)
                address += locationDTO?.district?.descriptionInfo?.localName + " - "

            if(locationDTO?.governorate)
                address += locationDTO?.governorate?.descriptionInfo?.localName + " - "

            if(locationDTO?.locality)
                address += locationDTO?.locality?.descriptionInfo?.localName + " - "

            if(locationDTO?.block)
                address += locationDTO?.block?.descriptionInfo?.localName + " - "

            if(locationDTO?.street)
                address += locationDTO?.street?.descriptionInfo?.localName + " - "

            if(locationDTO?.building)
                address += locationDTO?.building?.descriptionInfo?.localName + " - "

            if(locationDTO?.areaClass)
                address += locationDTO?.areaClass?.descriptionInfo?.localName + " - "

            if(value)
                address += value + " - "


            if(address.length()>0){
                address = address.substring(0, address.length()-2)
            }
            return address

        } else {
            return ""
        }
    }
}
