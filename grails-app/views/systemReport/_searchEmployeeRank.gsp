<el:formGroup>
    <el:select valueMessagePrefix="EnumDepartmentType"
               from="${ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.values()}"
               name="departmentType" size="8"  class=""
               label="${message(code:'department.departmentType.label',default:'departmentType')}" />
</el:formGroup>

<el:formGroup>

    <el:autocomplete multiple="true" optionKey="id" optionValue="name" size="8" class=""
                     controller="department" action="autocompleteHierarchy"
                     name="departmentIds" paramsGenerateFunction="departmentParams"
                     label="${message(code: 'department.label', default: 'department')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete multiple="true" optionKey="id" optionValue="name" size="8" class=""
                     controller="militaryRank" action="autocomplete"
                     name="militaryRankIds"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
</el:formGroup>


<script>

    function departmentParams() {
        var departmentTypeVal = $('#departmentType').val();
        return {departmentType: departmentTypeVal};
    }
</script>