package ps.gov.epsilon.hr.firm.profile.remoting

import grails.transaction.Transactional
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.dtos.v1.EmployeeDTO
import ps.gov.epsilon.hr.firm.profile.interfaces.v1.IEmployee

/**
 *<h1>Purpose</h1>
 * -expose Employee methods to public remoting usage-
 *@see EmployeeService
 **/
@Transactional
class RemoteEmployeeService implements IEmployee{

    static expose = ['httpinvoker']

    /**
     * to exclude properties when auto binding data.
     */
    static List EXCLUDED_PROPS = ['dateCreated','lastUpdated','serialVersionUID','metaClass','class']

    EmployeeService employeeService

    /**
     * get model entry.
     * @param SearchBean searchBean.
     * @return EmployeeDTO.
     * @see SearchBean.
     * @see EmployeeDTO.
     */
    @Override
    EmployeeDTO getEmployee(SearchBean searchBean) {
        EmployeeDTO employeeDTO
        try {
            PagedResultList dataList = employeeService.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            employeeDTO = (dataList?.toList()[0])?.toDTO(EmployeeDTO)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return employeeDTO
    }

    /**
     * search model entries.
     * @param SearchBean searchBean.
     * @return PagedList<EmployeeDTO>.
     * @see PagedList.
     * @see EmployeeDTO.
     */
    @Override
    PagedList<EmployeeDTO> searchEmployee(SearchBean searchBean) {
        PagedList<EmployeeDTO> pagedList = []
        try {
            PagedResultList dataList = employeeService.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            pagedList = new PagedList<EmployeeDTO>()
            pagedList.totalCount = dataList.totalCount
            pagedList.resultList = dataList?.toDTO(EmployeeDTO)
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
    String autoCompleteEmployee(SearchBean searchBean) {
        try {
            return employeeService.autoComplete(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))?.toString()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return ""
    }
}
