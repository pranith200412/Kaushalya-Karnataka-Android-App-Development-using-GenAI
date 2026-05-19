package com.kaushalya.karnataka.data

import com.kaushalya.karnataka.data.entity.Review
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.data.entity.Worker
import com.kaushalya.karnataka.data.repo.WorkerRepository

object SeedData {

    val CATEGORIES = listOf(
        "Electrician",
        "Plumber",
        "Carpenter",
        "Painter",
        "Mason",
        "Mechanic",
        "Tailor",
        "Welder",
    )

    suspend fun populateIfEmpty(repo: WorkerRepository) {
        if (repo.workerCount() > 0) return

        val seed = listOf(
            SeedWorker(
                Worker(
                    name = "Ramesh Naik",
                    category = "Electrician",
                    phone = "+91 90000 12345",
                    town = "Hubli",
                    bio = "20 years of wiring homes and small shops. Same-day visits within Hubli–Dharwad.",
                ),
                services = listOf(
                    "Fan Repair" to 200 to false,
                    "Full House Wiring" to 8000 to true,
                    "Inverter Installation" to 1500 to false,
                ),
                reviews = listOf(
                    Triple("Suresh K.", 5, "Came within an hour, fixed two ceiling fans. Honest pricing."),
                    Triple("Anita S.", 4, "Good work on the inverter. Cleaned up after."),
                ),
            ),
            SeedWorker(
                Worker(
                    name = "Lakshmi Devi",
                    category = "Tailor",
                    phone = "+91 90000 22222",
                    town = "Mysuru",
                    bio = "Blouse and saree fall stitching. 30+ years experience. Rush orders welcome.",
                ),
                services = listOf(
                    "Blouse Stitching" to 350 to false,
                    "Saree Fall + Pico" to 100 to false,
                    "Kurti Alteration" to 150 to true,
                ),
                reviews = listOf(
                    Triple("Bhavya R.", 5, "Perfect fit, finished in two days as promised."),
                    Triple("Meera P.", 5, "Best blouse-wali in our area, period."),
                    Triple("Pooja N.", 4, "Lovely work but a bit busy during festival season."),
                ),
            ),
            SeedWorker(
                Worker(
                    name = "Imran Sayed",
                    category = "Plumber",
                    phone = "+91 90000 33333",
                    town = "Bengaluru",
                    bio = "Leak repair, tap fitting, water-tank cleaning. Available 7am–9pm.",
                ),
                services = listOf(
                    "Leak Repair Visit" to 250 to true,
                    "Tap Replacement" to 400 to false,
                    "Tank Cleaning (1000L)" to 600 to false,
                ),
                reviews = listOf(
                    Triple("Karthik V.", 5, "Solved a kitchen-sink leak that two others couldn't."),
                    Triple("Rajesh M.", 4, "On time. Fair rate."),
                ),
            ),
            SeedWorker(
                Worker(
                    name = "Govinda Patil",
                    category = "Carpenter",
                    phone = "+91 90000 44444",
                    town = "Belagavi",
                    bio = "Custom cabinets, doors, and pooja-room woodwork. Brings own tools.",
                ),
                services = listOf(
                    "Door Repair" to 500 to true,
                    "Custom Wardrobe" to 12000 to true,
                    "Window Frame Fitting" to 800 to false,
                ),
                reviews = listOf(
                    Triple("Shobha L.", 5, "Beautiful pooja-mantap, exactly as designed."),
                ),
            ),
            SeedWorker(
                Worker(
                    name = "Mahesh Gowda",
                    category = "Painter",
                    phone = "+91 90000 55555",
                    town = "Mangaluru",
                    bio = "Interior + exterior painting, waterproofing. Free site visit & estimate.",
                ),
                services = listOf(
                    "Single Room Paint" to 2500 to true,
                    "Full House Paint" to 25000 to true,
                    "Wall Waterproofing" to 4000 to true,
                ),
                reviews = listOf(
                    Triple("Vinod B.", 4, "Clean job. Took an extra day but quality was good."),
                    Triple("Geetha R.", 5, "Loved the finish. Will hire again."),
                ),
            ),
            SeedWorker(
                Worker(
                    name = "Basavaraj Hiremath",
                    category = "Mason",
                    phone = "+91 90000 66666",
                    town = "Kalaburagi",
                    bio = "Brickwork, plastering, small repair jobs. Team of two available.",
                ),
                services = listOf(
                    "Wall Plastering (sqft)" to 35 to false,
                    "Compound Wall (running ft)" to 800 to true,
                ),
                reviews = listOf(
                    Triple("Naveen S.", 5, "Quick and clean compound-wall job, no drama."),
                ),
            ),
        )

        seed.forEach { sw ->
            val workerId = repo.upsertWorker(sw.worker)
            sw.services.forEach { (np, starting) ->
                val (name, price) = np
                repo.upsertService(
                    Service(
                        workerId = workerId,
                        name = name,
                        price = price,
                        isStartingAt = starting,
                    )
                )
            }
            sw.reviews.forEach { (reviewer, rating, comment) ->
                repo.addReview(
                    Review(
                        workerId = workerId,
                        reviewerName = reviewer,
                        rating = rating,
                        comment = comment,
                    )
                )
            }
        }
    }

    private data class SeedWorker(
        val worker: Worker,
        val services: List<Pair<Pair<String, Int>, Boolean>>,
        val reviews: List<Triple<String, Int, String>>,
    )
}
