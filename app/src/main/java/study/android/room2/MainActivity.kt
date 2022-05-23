package study.android.room2


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var rbStudent: RadioButton
    private lateinit var rbSubject: RadioButton
    private lateinit var spinner: Spinner
    private lateinit var listCaption: TextView
    private lateinit var recyclerView: RecyclerView



    val db by lazy {
        Room.databaseBuilder(
            this,
            SchoolDatabase::class.java, "school.db"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rbStudent = findViewById(R.id.rbStudent)
        rbSubject = findViewById(R.id.rbSubject)
        spinner = findViewById(R.id.spinner)
        listCaption = findViewById(R.id.listCaption)
        recyclerView = findViewById(R.id.recyclerView)

        rbStudent.setOnClickListener{
            listCaption.text = "Student's subjects"
            // так же должен меняться выпадающий список
            GlobalScope.launch {
                val s = db.schoolDao.getStudents()
                val st = MutableList<String>(0){""}
                for (i in s)
                    st.add(i.studentName)
                withContext(Main){
                    spinner.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, st )
                }
            }

        }

        rbSubject.setOnClickListener{
            listCaption.text = "Students study"
            // также должен меняться выпадающий список
            GlobalScope.launch {
                val s = db.schoolDao.getSubjects()
                val st = MutableList<String>(0){""}
                for (i in s)
                    st.add(i.subjectName)
                withContext(Main){
                    spinner.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, st )
                }
            }
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int, id: Long
            ) {
                GlobalScope.launch {
                    if (rbStudent.isChecked)
                    {
                        val s = db.schoolDao.getSubjectsOfStudent(spinner.adapter.getItem(position).toString())
                        val st = MutableList<String>(0){""}
                        for (i in s[0].subjects)
                            st.add(i.subjectName)
                        withContext(Main){
                           recyclerView.adapter = RecyclerView.Adapter<TextView>()
                        }
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        val dao = db.schoolDao;

        lifecycleScope.launch {
            DataExample.directors.forEach { dao.insertDirector(it) }
            DataExample.schools.forEach { dao.insertSchool(it) }
            DataExample.subjects.forEach { dao.insertSubject(it) }
            DataExample.students.forEach { dao.insertStudent(it) }
            DataExample.studentSubjectRelations.forEach { dao.insertStudentSubjectCrossRef(it) }
        }
    }

}