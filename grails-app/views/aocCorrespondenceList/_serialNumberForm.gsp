<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection" %>
<%
    def argValue = message(code: 'EnumCorrespondenceDirection.' + aocCorrespondenceList?.correspondenceDirection?.toString(),
            default: aocCorrespondenceList?.correspondenceDirection?.toString())
%>

<el:formGroup>
    <el:dateField name="${prefix}archivingDate" size="6" class=" ${serialClass}" isDisabled="${isReadOnly}"
                  label="${message(code: 'aocCorrespondenceList.archivingDate.label', args: [argValue])}"
                  value="${aocCorrespondenceList?.archivingDate}" isMaxDate="true"/>
    <el:textField name="${prefix}serialNumber" size="6" class=" ${serialClass}" isDisabled="${isReadOnly}"
                  label="${message(code: 'aocCorrespondenceList.serialNumber.label', args: [argValue])}"
                  value="${aocCorrespondenceList?.serialNumber}"/>
</el:formGroup>
