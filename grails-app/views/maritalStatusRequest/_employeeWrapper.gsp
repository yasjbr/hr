<el:formGroup>
    <el:labelField name="employeeName" size="8" label="${message(code: "employee.label", default: "employee")}"
                   value="${employee?.transientData?.personDTO?.localFullName}"/>
    <el:hiddenField name="employee.id"  value="${employee?.id}"/>

</el:formGroup>

<el:formGroup>
    <el:labelField name="currentEmployeeMilitaryRank" size="8"
                   label="${message(code: "employee.currentEmployeeMilitaryRank.label", default: "employee dateOfBirth")}"
                    
                   value="${employee?.currentEmployeeMilitaryRank?.militaryRank?.descriptionInfo?.localName}"/>
</el:formGroup>

<el:formGroup>
    <el:labelField name="employeeName" size="8"
                   label="${message(code: "employee.dateOfBirth.label", default: "employee dateOfBirth")}"
                     value="${employee?.transientData?.personDTO?.dateOfBirth?.dateTime?.date}"/>
</el:formGroup>

<el:formGroup>
    <el:labelField name="department" size="8" label="${message(code: "department.label", default: "department")}"
                   value="${employee?.currentEmploymentRecord?.department?.descriptionInfo?.localName}"/>
</el:formGroup>