package ps.gov.epsilon.hr.firm.remoting

import grails.gorm.PagedResultList
import grails.transaction.Transactional
import ps.gov.epsilon.hr.firm.FirmService
import ps.gov.epsilon.hr.firm.dtos.v1.FirmDTO
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.gov.epsilon.hr.firm.interfaces.v1.IFirm

@Transactional
class RemoteFirmService implements IFirm {

    static expose = ['httpinvoker']

    FirmService firmService

    @Override
    FirmDTO getFirm(SearchBean searchBean) {
        FirmDTO firmDTO
        try {
            PagedResultList dataList = firmService.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            firmDTO = (dataList?.toList()[0])?.toDTO(FirmDTO)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return firmDTO
    }

    @Override
    PagedList<FirmDTO> searchFirm(SearchBean searchBean) {
        PagedList<FirmDTO> pagedList = []
        try {
            PagedResultList dataList = firmService.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            pagedList = new PagedList<FirmDTO>()
            pagedList.totalCount = dataList.totalCount
            pagedList.resultList = dataList?.toDTO(FirmDTO)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return pagedList
    }

    @Override
    String autoCompleteFirm(SearchBean searchBean) {
        try {
            return firmService.autoComplete(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))?.toString()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return ""
    }
}
