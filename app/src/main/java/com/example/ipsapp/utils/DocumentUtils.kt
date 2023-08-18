package com.example.ipsapp.utils

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.apache.commons.lang3.tuple.MutablePair
import org.hl7.fhir.r4.hapi.ctx.FhirR4
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.Enumerations.FHIRDefinedType
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONObject

class DocumentUtils {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  fun getTitlesFromIpsDoc(doc: String): List<String> {
    val bundle = parser.parseResource(doc) as Bundle
    val composition =
      bundle.entry.firstOrNull { it.resource.resourceType == ResourceType.Composition }?.resource as Composition
    return composition.section.map { it.title }
  }

  fun getDataFromDoc(
    doc: String,
    title: String,
    map: MutableMap<String, MutablePair<List<String>, ArrayList<Resource>>>
  ): MutableMap<String, MutablePair<List<String>, ArrayList<Resource>>> {
    val bundle = parser.parseResource(doc) as Bundle
    val entryArray = bundle.entry.map { it.resource }

    val displayList = ArrayList<String>()
    val pair = MutablePair<List<String>, ArrayList<Resource>>()

    // Iterate through the entry array and filter based on criteria
    entryArray.asSequence()
      .filter { entry ->
        val resourceType = entry.resourceType.toString()
        getSearchingCondition(title, resourceType)
      }
      .forEach { element ->
        val code = element.hasCode()
        if (code != null) {
          val codingArray = code.coding

          val resourceList = if (pair.right != null && pair.right.isNotEmpty()) {
            pair.right.apply { add(element) }
          } else {
            arrayListOf(element)
          }
          pair.right = resourceList

          for (j in 0 until codingArray.size) {
            val display = codingArray[j].display
            displayList.add(display)
            break
          }
        }
      }
    pair.left = displayList
    map[title] = pair
    return map
  }

  private fun getSearchingCondition(resource: String, resourceType: String): Boolean {
    return when (resource) {
      "Allergies and Intolerances" -> resourceType == "AllergyIntolerance"
      "Medication" -> resourceType == "Medication"
      "Active Problems" -> resourceType == "Condition"
      "Immunizations" -> resourceType == "Immunization"
      "Results" -> resourceType == "Observation"

      "History of Past Illness" -> false // inside div
      "Plan of Treatment" -> false // inside div

      // titles have to change
      "procedure history" -> false
      "medical devices" -> false
      else -> false
    }
  }

  fun readFileFromAssets(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }
}
fun Resource.hasCode() : CodeableConcept? {
  return when(this.resourceType) {
    ResourceType.AllergyIntolerance -> (this as AllergyIntolerance).code
    ResourceType.Condition -> (this as Condition).code
    ResourceType.Medication -> (this as Medication).code
    ResourceType.Observation -> (this as Observation).code
    else -> null
  }
}
