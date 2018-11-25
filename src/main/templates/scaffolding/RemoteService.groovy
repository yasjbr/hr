<%=packageName ? "package ${packageName}" : ''%>

import grails.transaction.Transactional
import grails.gorm.PagedResultList
import ps.police.common.beans.${versionNumber}.PagedList
import ps.police.common.beans.${versionNumber}.SearchBean
import ps.police.common.beans.${versionNumber}.DeleteBean
import ps.police.common.utils.${versionNumber}.PCPUtils
import ${parentPackage}.${className}Service
import ${parentPackage}.commands.${versionNumber}.${className}Command
import ${parentPackage}.dtos.${versionNumber}.${className}DTO
import ${parentPackage}.interfaces.${versionNumber}.I${className}

/**
 *<h1>Purpose</h1>
 * -expose ${className} methods to public remoting usage-
 *@see ${className}Service
 **/
@Transactional
class Remote${className}Service implements I${className}{

    static expose = ['httpinvoker']

    /**
     * to exclude properties when auto binding data.
     */
    static List EXCLUDED_PROPS = ['dateCreated','lastUpdated','serialVersionUID','metaClass','class']

    ${className}Service ${propertyName}Service

    /**
     * get model entry.
     * @param SearchBean searchBean.
     * @return ${className}DTO.
     * @see SearchBean.
     * @see ${className}DTO.
     */
    @Override
    ${className}DTO get${className}(SearchBean searchBean) {
        ${className}DTO ${propertyName}DTO
        try {
            PagedResultList dataList = ${propertyName}Service.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            ${propertyName}DTO = (dataList?.toList()[0]).toDTO(${className}DTO)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return ${propertyName}DTO
    }

    /**
     * search model entries.
     * @param SearchBean searchBean.
     * @return PagedList<${className}DTO>.
     * @see PagedList.
     * @see ${className}DTO.
     */
    @Override
    PagedList<${className}DTO> search${className}(SearchBean searchBean) {
        PagedList<${className}DTO> pagedList = []
        try {
            PagedResultList dataList = ${propertyName}Service.search(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))
            pagedList = new PagedList<${className}DTO>()
            pagedList.totalCount = dataList.totalCount
            pagedList.resultList = dataList.toDTO(${className}DTO)
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
    String autoComplete${className}(SearchBean searchBean) {
        try {
            return ${propertyName}Service.autoComplete(PCPUtils.convertSearchBeanToGrailsParameterMap(searchBean))?.toString()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return ""
    }

    /**
     * save/update model entry.
     * @param SearchBean searchBean.
     * @return ${className}Command.
     * @see ${className}Command.
     */
    @Override
    ${className}Command save${className}(${className}Command ${propertyName}Command) {
        return PCPUtils.toCommand(${propertyName}Service.save(PCPUtils.convertCommandToParams(${propertyName}Command,EXCLUDED_PROPS)),${className}Command.class)
    }

    /**
     * delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    @Override
    DeleteBean delete${className}(DeleteBean deleteBean) {
        return ${propertyName}Service.delete(deleteBean)
    }
}
