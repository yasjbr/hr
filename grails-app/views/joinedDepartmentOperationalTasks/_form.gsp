<el:hiddenField name="department.id" value="${params?.department?.id}"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="operationalTask" action="autocomplete" name="operationalTask.id"
                     label="${message(code:'joinedDepartmentOperationalTasks.operationalTask.label',default:'operationalTask')}"
                     values="${[[joinedDepartmentOperationalTasks?.operationalTask?.id,joinedDepartmentOperationalTasks?.operationalTask?.descriptionInfo?.localName]]}" />
</el:formGroup>