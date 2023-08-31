package com.google.android.fhir.library.utils

import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Composition.SectionComponent
import org.hl7.fhir.r4.model.Narrative
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
// import ca.uhn.fhir.m

class DocumentGeneratorUtils {

  private val addedResourcesByType: MutableMap<String, MutableList<Resource>> = mutableMapOf()

  fun createResourceSection(resource: Resource): SectionComponent {
    val section = SectionComponent()

    section.title = getResourceTitle(resource)
    section.code = getResourceCode(resource)
    section.text = getResourceText(resource)

    val resourceType = resource.resourceType.toString()
    addedResourcesByType
      .getOrPut(resourceType) { mutableListOf() }
      .add(resource)

    section.entry.clear()
    addedResourcesByType[resourceType]?.distinctBy { it.idElement.toVersionless() }?.forEach { addedResource ->
      section.entry.add(Reference().setReference("${addedResource.idElement.toVersionless()}"))
    }
    return section
  }

  private fun getResourceText(resource: Resource): Narrative {
    val narrative = Narrative()
    narrative.statusAsString = "generated"
    return narrative
  }

  private fun getResourceCode(resource: Resource): CodeableConcept {
    val coding = Coding()
    coding.system = "http://loinc.org"
    val codeableConcept = CodeableConcept()
    codeableConcept.coding = listOf(coding)

    return when (resource.resourceType) {
      ResourceType.AllergyIntolerance -> {
        coding.code = "48765-2"
        coding.display = "Allergies and adverse reactions Document"
        codeableConcept
      }
      ResourceType.Condition -> {
        coding.code = "11450-4"
        coding.display = "Problem list Reported"
        codeableConcept
      }
      ResourceType.Medication -> {
        coding.code = "10160-0"
        coding.display = "History of Medication"
        codeableConcept
      }
      ResourceType.Immunization -> {
        coding.code = "11369-6"
        coding.display = "History of Immunizations"
        codeableConcept
      }
      ResourceType.Observation -> {
        coding.code = "30954-2"
        coding.display = "Test Results"
        codeableConcept
      }
      else -> {
        coding.system = "http://your-coding-system-url.com"
        coding.code = "12345"
        coding.display = "Display Text"
        codeableConcept
      }
    }
  }

  fun getResourceTitle(resource: Resource): String? {
    return when(resource.resourceType) {
      ResourceType.AllergyIntolerance -> "Allergies and Intolerances"
      ResourceType.Condition -> "Active Problem"
      ResourceType.Medication -> "Medication"
      ResourceType.Immunization -> "Immunizations"
      ResourceType.Observation -> "Results"
      else -> null
      // "History of Past Illness"
      // "Plan of Treatment"

    }
  }
}