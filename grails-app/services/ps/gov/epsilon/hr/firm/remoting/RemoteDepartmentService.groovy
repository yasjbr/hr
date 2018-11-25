package ps.gov.epsilon.hr.firm.remoting

import grails.transaction.Transactional
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.gov.epsilon.hr.firm.DepartmentService
import ps.gov.epsilon.hr.firm.dtos.v1.DepartmentDTO
import ps.gov.epsilon.hr.firm.interfaces.v1.IDepartment

/**
 *<h1>Purpose</h1>
 * -expose Department methods to public remoting usage-
 *@see DepartmentService
 **/
@Transactional
class RemoteDepartmentService implements IDepartment{

    static expose = ['httpinvoker']

    /**
     * to exclude properties when auto binding data.
     */
    static List EXCLUDED_PROPS = ['dateCreated','lastUpdated','serialVersionUID','metaClass','class']

    DepartmentService departmentService

    /**
     * get model entry.
     * @param SearchBean searchBean.
     * @return DepartmentDTO.
     * @see SearchBean.
     * @see DepartmentDTO.
     */
    @Override
    DepartmentDTO getDepartment(SearchBean searchBean) {
        DepartmentDTO departmentDTO
        try {
            PagedResultList dataList = departmentService.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            departmentDTO = (dataList?.toList()[0]).toDTO(DepartmentDTO)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return departmentDTO
    }

    /**
     * search model entries.
     * @param SearchBean searchBean.
     * @return PagedList<DepartmentDTO>.
     * @see PagedList.
     * @see DepartmentDTO.
     */
    @Override
    PagedList<DepartmentDTO> searchDepartment(SearchBean searchBean) {
        PagedList<DepartmentDTO> pagedList = []
        try {
            PagedResultList dataList = departmentService.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            pagedList = new PagedList<DepartmentDTO>()
            pagedList.totalCount = dataList.totalCount
            pagedList.resultList = dataList.toDTO(DepartmentDTO)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return pagedList
    }

    /**
     * auto complete model entries,info will be filled when specified in search bean.
     * @param SearchBean searchBean.
     * @return String JSON [id: ,name: ,info: ].
     */
    @Override
    String autoCompleteDepartment(SearchBean searchBean) {
        try {
            return departmentService.autoComplete(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))?.toString()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return ""
    }
}
