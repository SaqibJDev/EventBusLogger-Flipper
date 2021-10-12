package com.flipper.evenbuslogger.sample.ui.counter

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.flipper.evenbuslogger.sample.EvenBusRegistry
import io.bloco.template.R
import com.flipper.evenbuslogger.sample.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_counter.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CounterActivity : BaseActivity(), CounterView, LifecycleOwner {

    //    private lateinit var viewModel : CounterViewModel
    private lateinit var presenter: CounterPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        presenter = CounterPresenter(this)
//        viewModel = ViewModelProvider(this).get(CounterViewModel::class.java)
        lifecycle.addObserver(presenter)
        buttonIncrement.setOnClickListener { presenter.increment() }
        buttonDecrement.setOnClickListener { presenter.decrement() }
        buttonSave.setOnClickListener { presenter.save() }
        buttonRemove.setOnClickListener { presenter.remove() }

        MainScope().launch {
            EvenBusRegistry(activity = this@CounterActivity).registerSubscribers()
        }

//        viewModel.value()
//            .onEach { value.text = it.toString() }
//            .launchIn(lifecycleScope)

//        viewModel.errors()
//            .onEach {
//                showErrorSnackBar()
//            }
//            .launchIn(lifecycleScope)
//        }
    }

    override fun showErrorSnackBar() {
        Snackbar.make(coordinatorLayout, R.string.error_message, Snackbar.LENGTH_SHORT).show()
    }

    override fun setValue(count: Int) {
        value.text = count.toString()
    }

}
