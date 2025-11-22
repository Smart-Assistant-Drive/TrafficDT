import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/* * NOTA BENE: Questo codice richiede la libreria SnakeYAML.
 * Se stai usando Gradle, aggiungi questa dipendenza al tuo build.gradle:
 *
 * implementation("org.yaml:snakeyaml:2.0") // o la versione più recente
 */

// 1. CLASSE MODELLO: Rappresenta ogni singolo elemento nel file YAML.
// Usiamo una 'data class' di Kotlin per ottenere automaticamente getters, setters,
// hashCode, equals e toString, rendendo il codice molto più pulito.
data class RoadSegment(
    var roadId: String = "", // Usiamo 'var' e valori di default per SnakeYAML
    var direction: Int = 0,
    var numLanes: Int = 0,
    var numBlocks: Int = 0
)

// 2. CLASSE WRAPPER: Rappresenta la struttura radice del file YAML.
// La chiave principale è "segments".
data class RoadData(
    // SnakeYAML mapperá la chiave 'segments' del file YAML in questa lista.
    var segments: List<RoadSegment> = emptyList()
) {
    override fun toString(): String {
        return buildString {
            append("--- Dati Stradali Caricati ---\n")
            segments.forEach { segment ->
                append("  Segmento Stradale [ID=${segment.roadId}, Direzione=${segment.direction}")
                append(", Corsie=${segment.numLanes}, Blocchi=${segment.numBlocks}]\n")
            }
            append("------------------------------")
        }
    }
}

// 3. CLASSE PRINCIPALE: Contiene la logica per il parsing.
class YamlRoadReader {

    /**
     * Metodo per leggere una stringa YAML e deserializzarla nella classe RoadData.
     * @param yamlContent Il contenuto YAML da parsare.
     * @return L'oggetto RoadData popolato con i dati del file, o null in caso di errore.
     */
    fun readYamlString(yamlContent: String): RoadData? {
        val yaml = Yaml()
        return try {
            // Usa loadAs per mappare il contenuto YAML direttamente nella classe RoadData.
            yaml.loadAs(yamlContent, RoadData::class.java)
        } catch (e: Exception) {
            System.err.println("Errore durante la lettura o il parsing del YAML: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Metodo per leggere un file YAML da InputStream.
     * (Utile se si legge direttamente da un file o da risorse).
     */
    fun readYamlStream(inputStream: InputStream): RoadData? {
        val yaml = Yaml()
        return try {
            yaml.loadAs(inputStream, RoadData::class.java)
        } catch (e: Exception) {
            System.err.println("Errore durante la lettura del file YAML: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * NUOVA FUNZIONE: Legge il contenuto di un file YAML specificato dal percorso
     * e restituisce la lista di RoadSegment.
     * @param filePath Il percorso (path) del file YAML.
     * @return Una lista di RoadSegment, vuota se il file non è trovato o in caso di errore di parsing.
     */
    fun readYamlFile(filePath: String): List<RoadSegment> {
        val file = File(filePath)

        if (!file.exists()) {
            System.err.println("ERRORE: File non trovato al percorso: $filePath")
            return emptyList()
        }

        return try {
            val inputStream = FileInputStream(file)
            val roadData = readYamlStream(inputStream)

            // Ritorna la lista di segmenti, o una lista vuota se roadData è null
            roadData?.segments ?: emptyList()
        } catch (e: Exception) {
            System.err.println("ERRORE: Impossibile leggere il file: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}

// Funzione main per l'esecuzione e il test
fun main() {
    // Simula il contenuto di un file YAML multi-oggetto
    val sampleYaml = """
segments:
  - roadId: Via Centrale
    direction: 1
    numLanes: 4
    numBlocks: 25
  - roadId: Autostrada A10
    direction: -1
    numLanes: 6
    numBlocks: 12
  - roadId: Viale Est
    direction: 1
    numLanes: 2
    numBlocks: 8
""".trimIndent() // trimIndent per rimuovere gli spazi iniziali e mantenere la formattazione YAML

    println("Contenuto YAML da parsare:\n$sampleYaml\n")

    val reader = YamlRoadReader()

    // Esegue il parsing
    val roadData = reader.readYamlString(sampleYaml)

    // Stampa i risultati
    if (roadData != null) {
        println("Parsing completato con successo. Risultato:")
        println(roadData)

        // Esempio di accesso ai dati (stile Kotlin):
        println("\nAccesso al primo segmento:")
        val firstSegment = roadData.segments.firstOrNull()
        if (firstSegment != null) {
            println("ID Stradale: ${firstSegment.roadId}")
            println("Numero Corsie: ${firstSegment.numLanes}")
        }
    }
}