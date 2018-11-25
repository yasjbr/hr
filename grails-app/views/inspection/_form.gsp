<g:render template="/DescriptionInfo/wrapper" model="[bean: inspection?.descriptionInfo]"/>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="inspectionCategory"
                     action="autocomplete" name="inspectionCategory.id"
                     label="${message(code: 'inspection.inspectionCategory.label', default: 'inspectionCategory')}"
                     values="${[[inspection?.inspectionCategory?.id, inspection?.inspectionCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="orderId" size="8" class=" isRequired isNumber"
                     label="${message(code: 'inspection.orderId.label', default: 'orderId')}"
                     value="${inspection?.orderId}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="hasMark" size="8" class=""
                      label="${message(code: 'inspection.hasMark.label', default: 'hasMark')}"
                      value="${inspection?.hasMark}" isChecked="${inspection?.hasMark}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="hasPeriod" size="8" class=""
                      label="${message(code: 'inspection.hasPeriod.label', default: 'hasPeriod')}"
                      value="${inspection?.hasPeriod}" isChecked="${inspection?.hasPeriod}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="hasDates" size="8" class=""
                      label="${message(code: 'inspection.hasDates.label', default: 'hasDates')}"
                      value="${inspection?.hasDates}" isChecked="${inspection?.hasDates}"/>
</el:formGroup>
%{--<el:formGroup>
    <el:checkboxField name="isIncludedInLists" size="8" class=""
                      label="${message(code: 'inspection.isIncludedInLists.label', default: 'isIncludedInLists')}"
                      value="${inspection?.isIncludedInLists}" isChecked="${inspection?.isIncludedInLists}"/>
</el:formGroup>--}%
<el:hiddenField name="isIncludedInLists" value="false" />
<el:formGroup>
    <el:textArea
            name="description"
            size="8" class=""
            label="${message(code: 'inspection.description.label', default: 'description')}"
            value="${inspection?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'inspection.note.label', default: 'note')}"
                 value="${inspection?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'inspection.universalCode.label', default: 'universalCode')}"
                  value="${inspection?.universalCode}"/>
</el:formGroup>
<el:formGroup>
    <el:dualListBox size="8" optionKey="id" from="${committeeRoleList}"
                    values="${inspection?.committeeRoles?.committeeRole}"
                    label="${message(code: 'inspection.committeeRole.label', default: 'committee role')}"
                    name="committeeRoles.id"
                    moveOnSelect="false"
                    showFilterInputs="true"
                    isAllowToAdd="true"/>
</el:formGroup>







