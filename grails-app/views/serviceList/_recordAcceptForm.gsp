<% def size= colSize?:6 %>
<g:if test="${size == 6}">
    <el:dateField name="dateEffective" size="6" class=" isRequired"
                  label="${message(code: 'serviceListEmployee.dateEffective.label', default: 'dateEffective')}"
                  value=""/>
</g:if>
<g:else>
    <el:dateField name="dateEffective" size="${size}" class=" isRequired"
                  label="${message(code: 'serviceListEmployee.dateEffective.label', default: 'dateEffective')}"
                  value=""/>
</g:else>