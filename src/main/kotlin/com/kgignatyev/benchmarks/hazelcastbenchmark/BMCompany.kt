package com.kgignatyev.benchmarks.hazelcastbenchmark


class BMCompany {
    var id: String = ""
    var name: String = ""
    var industry: String = ""
    var url: String = ""
    var catchPhrase: String = ""
    var employees: MutableList<BMEmployee> = mutableListOf()
}

class BMEmployee {
    var fullName:String = ""
    var title:String = ""
}
