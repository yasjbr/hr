<%=packageName ? "package ${packageName}" : ''%>

import ps.police.common.beans.${versionNumber}.PagedList
import ps.police.common.beans.${versionNumber}.SearchBean
import ps.police.common.beans.${versionNumber}.DeleteBean
import ${parentPackage}.commands.${versionNumber}.${className}Command
import ${parentPackage}.dtos.${versionNumber}.${className}DTO

/**
 *<h1>Purpose</h1>
 * hold ${className} exposed methods.
 **/
interface I${className} {
    ${className}DTO get${className}(SearchBean searchBean)
    PagedList<${className}DTO> search${className}(SearchBean searchBean)
    String autoComplete${className}(SearchBean searchBean)
    ${className}Command save${className}(${className}Command ${propertyName}Bean)
    DeleteBean delete${className}(DeleteBean deleteBean)
}